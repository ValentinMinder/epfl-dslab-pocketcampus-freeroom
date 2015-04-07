package org.pocketcampus.plugin.events.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.BackgroundTasker.Scheduler;
import org.pocketcampus.platform.server.EmailSender;
import org.pocketcampus.platform.server.TaskRunner;
import org.pocketcampus.platform.server.EmailSender.EmailTemplateInfo;
import org.pocketcampus.platform.server.EmailSender.SendEmailInfo;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.events.server.decoders.EventItemDecoder;
import org.pocketcampus.plugin.events.server.decoders.EventPoolDecoder;
import org.pocketcampus.plugin.events.server.importers.MementoImporter;
import org.pocketcampus.plugin.events.server.utils.DBUtils;
import org.pocketcampus.plugin.events.server.utils.Utils;
import org.pocketcampus.plugin.events.shared.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * Class that takes care of the services the Events server provides to the
 * client
 * 
 * @author Amer C <amer.chamseddine@epfl.ch>
 * 
 */
public class EventsServiceImpl implements EventsService.Iface, StateChecker, TaskRunner {

	private ConnectionManager connMgr;
	
	public EventsServiceImpl() {
		connMgr = new ConnectionManager(PocketCampusServer.CONFIG.getString("DB_URL"),
				PocketCampusServer.CONFIG.getString("DB_USERNAME"), PocketCampusServer.CONFIG.getString("DB_PASSWORD"));
	}

	@Override
	public int checkState() throws IOException {
		try {
			EventPool root = EventPoolDecoder.eventPoolFromDb(connMgr.getConnection(), Constants.CONTAINER_EVENT_ID);
			return (root != null ? 200 : 550);
		} catch (SQLException e) {
			e.printStackTrace();
			return 500;
		}
	}
	
	@Override
	public void schedule(Scheduler tasker) {
		tasker.addTask(60 * 60 * 1000, false, new Runnable() {
			public void run() {
				MementoImporter.importFromMemento(connMgr);
			}
		});
	}
	
	@Override
	public EventItemReply getEventItem(EventItemRequest req) throws TException {
		List<String> tokens = (req.isSetUserTickets() ? req.getUserTickets() : new LinkedList<String>());
		Utils.registerForPush(tokens);
		long parentId = req.getEventItemId();
		// if(req.isSetUserToken()) tokens.add(req.getUserToken()); // backward compatibility
		try {
			Connection conn = connMgr.getConnection();
			DBUtils.logPageView(conn, tokens, parentId, "eventitem");
			EventItem item = EventItemDecoder.eventItemFromDb(conn, parentId, tokens, req.getLang());
			if (item == null)
				return new EventItemReply(400);
			Utils.fixCategAndTags(item);
			Map<Long, EventPool> childrenPools = EventPoolDecoder.eventPoolsFromDb(conn, parentId);
			item.setChildrenPools(new LinkedList<Long>(childrenPools.keySet())); // over-ride children (must be the same)
			EventItemReply reply = new EventItemReply(200);
			reply.setEventItem(item);
			reply.setChildrenPools(childrenPools);
			reply.setTags(DBUtils.getTagsFromDb(conn, false, req.getLang()));
			reply.setCategs(DBUtils.getCategsFromDb(conn, req.getLang()));
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventItemReply(500);
		}
	}

	@Override
	public EventPoolReply getEventPool(EventPoolRequest req) throws TException {
		List<String> tokens = (req.isSetUserTickets() ? req.getUserTickets() : new LinkedList<String>());
		Utils.registerForPush(tokens);
		long parentId = req.getEventPoolId();
		int periodInHours = (req.isSetPeriodInHours() ? req.getPeriodInHours() : (req.isSetPeriod() ? (req.getPeriod() * 24) : 30 * 24));
		if (req.isFetchPast())
			periodInHours = -periodInHours;
		if (parentId != Constants.CONTAINER_EVENT_ID)
			periodInHours = 0;
		try {
			Connection conn = connMgr.getConnection();
			DBUtils.logPageView(conn, tokens, parentId, "eventpool");
			EventPool pool = EventPoolDecoder.eventPoolFromDb(conn, parentId);
			if (pool == null)
				return new EventPoolReply(400);
			Map<Long, EventItem> childrenItems;
			if (pool.isSendStarredItems()) {
				if (!req.isSetStarredEventItems() || !pool.isSetParentEvent())
					return new EventPoolReply(400);
				pool.setChildrenEvents(DBUtils.filterStarred(conn, req.getStarredEventItems(), pool.getParentEvent()));
				childrenItems = EventItemDecoder.eventItemsByIds(conn, pool.getChildrenEvents(), tokens, req.getLang());
			} else {
				childrenItems = EventItemDecoder.eventItemsFromDb(conn, parentId, periodInHours * 60, tokens, req.getLang());
			}
			for (EventItem e : childrenItems.values())
				Utils.fixCategAndTags(e);
			pool.setChildrenEvents(new LinkedList<Long>(childrenItems.keySet())); // override children because of filtering logic
			EventPoolReply reply = new EventPoolReply(200);
			reply.setEventPool(pool);
			reply.setChildrenItems(childrenItems);
			reply.setTags(DBUtils.getTagsFromDb(conn, false, req.getLang()));
			reply.setCategs(DBUtils.getCategsFromDb(conn, req.getLang()));
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventPoolReply(500);
		}
	}

	@Override
	public SendEmailReply sendStarredItemsByEmail(SendEmailRequest req) throws TException {
		try {
			Connection conn = connMgr.getConnection();
			EventPool pool = EventPoolDecoder.eventPoolFromDb(conn, req.getEventPoolId());
			if (pool == null || !pool.isSendStarredItems() || !req.isSetStarredEventItems() || !pool.isSetParentEvent() || !req.isSetEmailAddress())
				return new SendEmailReply(400);
			pool.setChildrenEvents(DBUtils.filterStarred(conn, req.getStarredEventItems(), pool.getParentEvent()));
			List<String> tokens = new LinkedList<String>();
			if (req.isSetUserTickets() && req.getUserTickets().size() > 0)
				tokens = req.getUserTickets();
			EventItem mainEvent = EventItemDecoder.eventItemFromDb(conn, pool.getParentEvent(), tokens, req.getLang());
			Map<Long, EventItem> childrenItems = EventItemDecoder.eventItemsByIds(conn, pool.getChildrenEvents(), tokens, req.getLang());
			for (EventItem e : childrenItems.values())
				Utils.fixCategAndTags(e);
			Map<Integer, String> categMap = DBUtils.getCategsFromDb(conn, req.getLang());
			Map<Integer, List<EventItem>> eventsByCateg = new HashMap<Integer, List<EventItem>>();
			for (EventItem e : childrenItems.values()) {
				if (!eventsByCateg.containsKey(e.getEventCateg()))
					eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
				eventsByCateg.get(e.getEventCateg()).add(e);
			}
			List<Integer> categs = new LinkedList<Integer>(eventsByCateg.keySet());
			Collections.sort(categs);
			StringBuilder emailBuilder = new StringBuilder();
			emailBuilder.append("<table style=\"background-color: #F1F1F1; border-spacing: 5px 5px; border: 1px solid #E0E0E0; width: 100%;\">\n");
			for (int c : categs) {
				List<EventItem> categEvents = eventsByCateg.get(c);
				Collections.sort(categEvents, new Comparator<EventItem>() {
					public int compare(EventItem lhs, EventItem rhs) {
						if (lhs.isSetStartDate() && rhs.isSetStartDate()) {
							return Long.valueOf(lhs.getStartDate()).compareTo(rhs.getStartDate());
						}
						if (lhs.isSetEventTitle() && rhs.isSetEventTitle()) {
							return lhs.getEventTitle().compareTo(rhs.getEventTitle());
						}
						return 0;
					}
				});
				StringBuilder categBuilder = new StringBuilder();
				categBuilder.append("<tr><td style=\"border: 0px; height: 20px; padding: 0px; background-color: #F1F1F1; font:1em Georgia,serif;\" colspan=\"2\">"
						+ categMap.get(c) + "</td></tr>\n");
				for (EventItem e : categEvents) {
					categBuilder.append("<tr>\n");
					categBuilder.append("<td style=\"border: 0px; background-color: #F1F1F1; padding-top: 6px; vertical-align: top;\">\n");
					categBuilder.append("<div><img style=\"width: 80px\" src=\"" + (e.isSetEventThumbnail() ? e.getEventThumbnail() : "") + "\"></div>\n");
					categBuilder.append("</td>\n");
					categBuilder.append("<td><table style=\"background-color: #F1F1F1; border-spacing: 0px 5px; width: 100%;\">\n");

					categBuilder.append("<tr><td style=\"background-color: #FFFFFF; border: 1px solid #E0E0E0; padding:10px;\">\n");
					categBuilder.append("<div style=\"font:1.1em Georgia,serif; font-weight:bold;\">" + (e.isSetEventTitle() ? e.getEventTitle() : "") + "</div>\n");
					categBuilder.append("<div style=\"font:1em Georgia,serif; text-align: left;\">"
							+ (e.isSetSecondLine() ? e.getSecondLine() : (e.isSetEventSpeaker() ? e.getEventSpeaker() : "")) + "</div>\n");
					categBuilder.append("</td></tr>\n");
					categBuilder.append("<tr><td style=\"background-color: #FFFFFF; border: 1px solid #E0E0E0; padding:10px;\">\n");
					categBuilder.append("<div style=\"font:1em Georgia,serif; text-align: left;\">" + (e.isSetEventDetails() ? e.getEventDetails() : "") + "</div>\n");
					categBuilder.append("</td></tr>\n");

					categBuilder.append("</table></td>\n");
					categBuilder.append("</tr>\n");
				}
				emailBuilder.append(categBuilder.toString());
			}
			emailBuilder.append("</table>\n");
			boolean res = EmailSender.openSession();
			res = res && EmailSender.sendEmail(req.getEmailAddress(), "Your Starred Items in " + mainEvent.getEventTitle(), emailBuilder.toString());
			res = res && EmailSender.closeSession();
			System.out.println("send email with " + childrenItems.size() + " items, success=" + res);
			return new SendEmailReply(res ? 200 : 500);
		} catch (SQLException e) {
			e.printStackTrace();
			return new SendEmailReply(500);
		}
	}

	@Override
	public AdminSendRegEmailReply adminSendRegistrationEmail(final AdminSendRegEmailRequest iRequest) throws TException {
		Runnable sender = new Runnable() {
			public void run() {
				try {
					Connection conn = connMgr.getConnection();
					PreparedStatement stm;
					ResultSet rs;
					EmailTemplateInfo template = null;
					stm = conn.prepareStatement("SELECT participantsPool,emailTitle,emailBody,sendOnlyTo FROM eventemails WHERE templateId=?;");
					stm.setString(1, iRequest.getTemplateId());
					rs = stm.executeQuery();
					if (rs.next())
						template = new EmailTemplateInfo(rs.getLong(1), rs.getString(2), rs.getString(3), (rs.getString(4) == null ? null : Utils.arrayToList(rs.getString(4)
								.split("[,]"))));
					rs.close();
					stm.close();
					if (template == null) {
						System.out.println("ERROR invalid template id");
						return;
					}
					List<SendEmailInfo> emails = new LinkedList<SendEmailInfo>();
					stm = conn
							.prepareStatement("SELECT emailAddress,userId,addressingName FROM eventusers WHERE mappedEvent IN (SELECT eventId FROM eventitems WHERE parentPool=?);");
					stm.setLong(1, template.getParticipantsPool());
					rs = stm.executeQuery();
					while (rs.next()) {
						if (iRequest.isSetSendOnlyTo() && !iRequest.getSendOnlyTo().contains(rs.getString(1)))
							continue;
						if (template.getSendOnlyTo() != null && !template.getSendOnlyTo().contains(rs.getString(1)))
							continue;
						emails.add(new SendEmailInfo(rs.getString(1), rs.getString(2), rs.getString(3)));
					}
					rs.close();
					stm.close();
					System.out.println("Should send " + emails.size() + " registration emails.");
					int count = 0;
					EmailSender.openSession();
					for (SendEmailInfo sei : emails) {
						String emailBody = template.getEmailBody().replace("PARTICIPANT_NAME", sei.getAddressingName()).replace("PARTICIPANT_TOKEN", sei.getUserToken());
						EmailSender.sendEmailP(sei.getEmailAddress(), template.getEmailTitle(), emailBody);
						System.out.println(++count + ". sent email to " + sei.getEmailAddress());
					}
					EmailSender.closeSession();
					System.out.println("Finished sending " + emails.size() + " registration emails");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(sender).start();
		return new AdminSendRegEmailReply(200);
	}

	@Override
	@Deprecated
	public ExchangeReply exchangeContacts(ExchangeRequest req) throws TException {
		try {
			Connection conn = connMgr.getConnection();
			String userToken = DBUtils.userTokenFromExchangeToken(conn, req.getExchangeToken());
			if (userToken == null)
				return new ExchangeReply(400);
			System.out.println("Exchannging contact information between " + req.getUserToken() + " and " + userToken);
			int res = DBUtils.exchangeContacts(conn, req.getUserToken(), userToken);
			if (res != 2) {
				System.out.println("Failed");
				return new ExchangeReply(400);
			}
			System.out.println("Succeess");
			return new ExchangeReply(200);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ExchangeReply(500);
		}
	}

}
