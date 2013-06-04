package org.pocketcampus.plugin.events.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.thrift.TException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.events.shared.AdminSendRegEmailReply;
import org.pocketcampus.plugin.events.shared.AdminSendRegEmailRequest;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventItemReply;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventPool;
import org.pocketcampus.plugin.events.shared.EventPoolReply;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService;
import org.pocketcampus.plugin.events.shared.ExchangeReply;
import org.pocketcampus.plugin.events.shared.ExchangeRequest;
import org.pocketcampus.plugin.events.shared.SendEmailReply;
import org.pocketcampus.plugin.events.shared.SendEmailRequest;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

/**
 * 
 * Class that takes care of the services the Events server provides to the
 * client
 * 
 * @author Amer C <amer.chamseddine@epfl.ch>
 * 
 */
public class EventsServiceImpl implements EventsService.Iface {
	
	private ConnectionManager connMgr;
	private String dateLastImport = "";
	private Runnable importer = new Runnable() {
		public void run() {
			System.out.println("Started Async Import from Memento on " + dateLastImport);
			try {
				HashMap<String, String> feedsMap = new HashMap<String, String>();
				Connection conn = connMgr.getConnection();
				for(String uri : collectUris(conn)) {
					try {
						EventItem ei = parseEvent(uri, feedsMap);
						readFromIcs(ei);
						updateEventItem(ei, conn);
					} catch (IOException e) {
						System.out.println("AIE IOException, skipping event=" + uri + "... " + e.getMessage());
					} catch (ParserException e) {
						System.out.println("AIE ParserException, skipping event=" + uri + "... " + e.getMessage());
					} catch (ParseException e) {
						System.out.println("AIE ParseException, skipping event=" + uri + "... " + e.getMessage());
					}
				}
				for(String key : feedsMap.keySet()) {
					updateEventTag(key, feedsMap.get(key), conn, true);
				}
				System.out.println("Finished Async Import on " + dateLastImport);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	

	public EventsServiceImpl() {
		System.out.println("Starting Events plugin server ...");
		try {
			connMgr = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	String [] ballouta = new String [] {"http://api.androidhive.info/music/images/adele.png","http://api.androidhive.info/music/images/eminem.png","http://api.androidhive.info/music/images/mj.png","http://api.androidhive.info/music/images/rihanna.png","http://api.androidhive.info/music/images/arrehman.png","http://api.androidhive.info/music/images/alexi_murdoch.png","http://api.androidhive.info/music/images/dido.png","http://api.androidhive.info/music/images/enrique.png","http://api.androidhive.info/music/images/ennio.png","http://api.androidhive.info/music/images/backstreet_boys.png","http://api.androidhive.info/music/images/adele.png","http://api.androidhive.info/music/images/eminem.png","http://api.androidhive.info/music/images/mj.png","http://api.androidhive.info/music/images/rihanna.png","http://api.androidhive.info/music/images/arrehman.png","http://api.androidhive.info/music/images/alexi_murdoch.png","http://api.androidhive.info/music/images/dido.png","http://api.androidhive.info/music/images/enrique.png","http://api.androidhive.info/music/images/ennio.png","http://api.androidhive.info/music/images/backstreet_boys.png","http://api.androidhive.info/music/images/adele.png","http://api.androidhive.info/music/images/eminem.png","http://api.androidhive.info/music/images/mj.png","http://api.androidhive.info/music/images/rihanna.png","http://api.androidhive.info/music/images/arrehman.png","http://api.androidhive.info/music/images/alexi_murdoch.png","http://api.androidhive.info/music/images/dido.png","http://api.androidhive.info/music/images/enrique.png","http://api.androidhive.info/music/images/ennio.png","http://api.androidhive.info/music/images/backstreet_boys.png"};
	
	@Override
	public EventItemReply getEventItem(EventItemRequest req) throws TException {
		System.out.println("getEventItem id=" + req.getEventItemId());
		importFromMemento();
		long parentId = req.getEventItemId();
		List<String> tokens = (req.isSetUserTickets() ? req.getUserTickets() : new LinkedList<String>());
		//if(req.isSetUserToken()) tokens.add(req.getUserToken()); // backward compatibility
		try {
			Connection conn = connMgr.getConnection();
			EventItem item = eventItemFromDb(conn, parentId, tokens);
			if(item == null)
				return new EventItemReply(400);
			fixCategAndTags(item);
			Map<Long, EventPool> childrenPools = eventPoolsFromDb(conn, parentId);
			item.setChildrenPools(new LinkedList<Long>(childrenPools.keySet())); // over-ride children (must be the same)
			EventItemReply reply = new EventItemReply(200);
			reply.setEventItem(item);
			reply.setChildrenPools(childrenPools);
			reply.setTags(getTagsFromDb(conn, false));
			reply.setCategs(getCategsFromDb(conn));
			System.out.println("returned " + reply.getChildrenPools().size() + " pools");
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventItemReply(500);
		}
	}
	
	@Override
	public EventPoolReply getEventPool(EventPoolRequest req) throws TException {
		System.out.println("getEventPool id=" + req.getEventPoolId());
		importFromMemento();
		long parentId = req.getEventPoolId();
		int period = (req.isSetPeriod() ? req.getPeriod() : 1);
		if(req.isFetchPast()) period = -period;
		if(parentId != Constants.CONTAINER_EVENT_ID) period = 0;
		List<String> tokens = (req.isSetUserTickets() ? req.getUserTickets() : new LinkedList<String>());
		//if(req.isSetUserToken()) tokens.add(req.getUserToken()); // backward compatibility
		try {
			Connection conn = connMgr.getConnection();
			EventPool pool = eventPoolFromDb(conn, parentId);
			if(pool == null)
				return new EventPoolReply(400);
			Map<Long, EventItem> childrenItems;
			if(pool.isSendStarredItems()) {
				if(!req.isSetStarredEventItems() || !pool.isSetParentEvent())
					return new EventPoolReply(400);
				pool.setChildrenEvents(filterStarred(conn, req.getStarredEventItems(), pool.getParentEvent()));
				childrenItems = eventItemsByIds(conn, pool.getChildrenEvents(), tokens);
			} else {
				childrenItems = eventItemsFromDb(conn, parentId, period, tokens);
			}
			for(EventItem e : childrenItems.values())
				fixCategAndTags(e);
			pool.setChildrenEvents(new LinkedList<Long>(childrenItems.keySet())); // override children because of filtering logic
			EventPoolReply reply = new EventPoolReply(200);
			reply.setEventPool(pool);
			reply.setChildrenItems(childrenItems);
			reply.setTags(getTagsFromDb(conn, false));
			reply.setCategs(getCategsFromDb(conn));
			System.out.println("returned " + reply.getChildrenItems().size() + " items");
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventPoolReply(500);
		}
	}

	@Override
	@Deprecated
	public ExchangeReply exchangeContacts(ExchangeRequest req) throws TException {
		System.out.println("exchangeContacts");
		try {
			Connection conn = connMgr.getConnection();
			String userToken = userTokenFromExchangeToken(conn, req.getExchangeToken());
			if(userToken == null)
				return new ExchangeReply(400);
			System.out.println("Exchannging contact information between " + req.getUserToken() + " and " + userToken);
			int res = exchangeContacts(conn, req.getUserToken(), userToken);
			if(res != 2) {
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
	
	@Override
	public SendEmailReply sendStarredItemsByEmail(SendEmailRequest req) throws TException {
		System.out.println("sendStarredItemsByEmail");
		try {
			Connection conn = connMgr.getConnection();
			EventPool pool = eventPoolFromDb(conn, req.getEventPoolId());
			if(pool == null || !pool.isSendStarredItems() || !req.isSetStarredEventItems() || !pool.isSetParentEvent() || !req.isSetEmailAddress())
				return new SendEmailReply(400);
			pool.setChildrenEvents(filterStarred(conn, req.getStarredEventItems(), pool.getParentEvent()));
			List<String> tokens = new LinkedList<String>();
			if(req.isSetUserTickets() && req.getUserTickets().size() > 0) tokens = req.getUserTickets();
			EventItem mainEvent = eventItemFromDb(conn, pool.getParentEvent(), tokens);
			Map<Long, EventItem> childrenItems = eventItemsByIds(conn, pool.getChildrenEvents(), tokens);
			for(EventItem e : childrenItems.values())
				fixCategAndTags(e);
			Map<Integer, String> categMap = getCategsFromDb(conn);
			Map<Integer, List<EventItem>> eventsByCateg = new HashMap<Integer, List<EventItem>>();
			for(EventItem e : childrenItems.values()) {
				if(!eventsByCateg.containsKey(e.getEventCateg()))
					eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
				eventsByCateg.get(e.getEventCateg()).add(e);
			}
			List<Integer> categs = new LinkedList<Integer>(eventsByCateg.keySet());
			Collections.sort(categs);
			StringBuilder emailBuilder = new StringBuilder();
			emailBuilder.append("<table style=\"background-color: #F1F1F1; border-spacing: 5px 5px; border: 1px solid #E0E0E0; width: 100%;\">\n");
			for(int c : categs) {
				List<EventItem> categEvents = eventsByCateg.get(c);
				Collections.sort(categEvents, new Comparator<EventItem>() {
					public int compare(EventItem lhs, EventItem rhs) {
						if(lhs.isSetStartDate() && rhs.isSetStartDate()) {
							return Long.valueOf(lhs.getStartDate()).compareTo(rhs.getStartDate());
						}
						if(lhs.isSetEventTitle() && rhs.isSetEventTitle()) {
							return lhs.getEventTitle().compareTo(rhs.getEventTitle());
						}
						return 0;
					}
				});
				StringBuilder categBuilder = new StringBuilder();
				categBuilder.append("<tr><td style=\"border: 0px; height: 20px; padding: 0px; background-color: #F1F1F1; font:1em Georgia,serif;\">" + categMap.get(c) + "</td></tr>\n");
				for(EventItem e : categEvents) {
					categBuilder.append("<tr>\n");
					categBuilder.append("<td style=\"border: 0px; background-color: #F1F1F1; padding-top: 6px; vertical-align: top;\">\n");
					categBuilder.append("<div><img style=\"width: 80px\" src=\"" + (e.isSetEventThumbnail() ? e.getEventThumbnail() : "") + "\"></div>\n");
					categBuilder.append("</td>\n");
					categBuilder.append("<td><table style=\"background-color: #F1F1F1; border-spacing: 0px 5px; width: 100%;\">\n");
					
					categBuilder.append("<tr><td style=\"background-color: #FFFFFF; border: 1px solid #E0E0E0; padding:10px;\">\n");
					categBuilder.append("<div style=\"font:1.1em Georgia,serif; font-weight:bold;\">" + (e.isSetEventTitle() ? e.getEventTitle() : "") + "</div>\n");
					categBuilder.append("<div style=\"font:1em Georgia,serif; text-align: left;\">" + (e.isSetSecondLine() ? e.getSecondLine() : (e.isSetEventSpeaker() ? e.getEventSpeaker() : "")) + "</div>\n");
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
			boolean res = GmailSender.sendEmail(req.getEmailAddress(), "Your Starred Items in " + mainEvent.getEventTitle(), emailBuilder.toString());
			System.out.println("send email with " + childrenItems.size() + " items, success=" + res);
			return new SendEmailReply(res ? 200 : 500);
		} catch (SQLException e) {
			e.printStackTrace();
			return new SendEmailReply(500);
		}
	}
	
	@Override
	public AdminSendRegEmailReply adminSendRegistrationEmail(AdminSendRegEmailRequest iRequest) throws TException {
		System.out.println("adminSendRegistrationEmail");
		try {
			Connection conn = connMgr.getConnection();
			PreparedStatement stm;
			ResultSet rs;
			EmailTemplateInfo template = null;
			stm = conn.prepareStatement("SELECT participantsPool,emailTitle,emailBody,sendOnlyTo FROM eventemails WHERE templateId=?;");
			stm.setString(1, iRequest.getTemplateId());
			rs = stm.executeQuery();
			if(rs.next())
				template = new EmailTemplateInfo(rs.getLong(1), rs.getString(2), rs.getString(3), (rs.getString(4) == null ? null : arrayToList(rs.getString(4).split("[,]"))));
			rs.close();
			stm.close();
			if(template == null)
				return new AdminSendRegEmailReply(400);
			List<SendEmailInfo> emails = new LinkedList<EventsServiceImpl.SendEmailInfo>();
			stm = conn.prepareStatement("SELECT emailAddress,userId,addressingName FROM eventusers WHERE mappedEvent IN (SELECT eventId FROM eventitems WHERE parentPool=?);");
			stm.setLong(1, template.getParticipantsPool());
			rs = stm.executeQuery();
			while(rs.next()) {
				if(iRequest.isSetSendOnlyTo() && !iRequest.getSendOnlyTo().contains(rs.getString(1)))
					continue;
				if(template.getSendOnlyTo() != null && !template.getSendOnlyTo().contains(rs.getString(1)))
					continue;
				emails.add(new SendEmailInfo(rs.getString(1), rs.getString(2), rs.getString(3)));
			}
			rs.close();
			stm.close();
			System.out.println("Should send " + emails.size() + " emails.");
			boolean succ = true;
			for(SendEmailInfo sei : emails) {
				String emailBody = template.getEmailBody().replace("PARTICIPANT_NAME", sei.getAddressingName()).replace("PARTICIPANT_TOKEN", sei.getUserToken());
				boolean res = GmailSender.sendEmail(sei.getEmailAddress(), template.getEmailTitle(), emailBody);
				succ = succ && res;
				System.out.println("send email to " + sei.getEmailAddress() + ", success=" + res);
			}
			System.out.println("Finished sending reg emails, success=" + succ);
			return new AdminSendRegEmailReply(succ ? 200 : 500);
		} catch (SQLException e) {
			e.printStackTrace();
			return new AdminSendRegEmailReply(500);
		}
	}
	
	/*private void addFeaturedEvents(Map<Long, EventItem> eventItems) throws ParseException {
		EventItem edicOpenHouse = new EventItem(12000000l);
		edicOpenHouse.setEventCateg(-1);
		edicOpenHouse.setEventTitle("EDIC - Open House 2013");
		edicOpenHouse.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T000000").getTime());
		edicOpenHouse.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130323T000000").getTime());
		edicOpenHouse.setFullDay(true);
		edicOpenHouse.setEventDetails("The EDIC Open House is a gathering of prospective PhD students (admitted from the January 15th application deadline) and the I&C School community, to learn about our program, the university and the charming Lake Geneva Region. ");
		edicOpenHouse.setChildrenPools(Arrays.asList(new Long[] {12000001l, 12000002l, 12000003l, 12000004l}));
		eventItems.put(edicOpenHouse.getEventId(), edicOpenHouse);
		
		
		EventItem openHouseTalk1 = new EventItem(12000001l);
		openHouseTalk1.setEventCateg(-1);
		openHouseTalk1.setEventTitle("Open House Talk 1");
		openHouseTalk1.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T100000").getTime());
		openHouseTalk1.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T110000").getTime());
		openHouseTalk1.setFullDay(false);
		openHouseTalk1.setEventPlace("BC 410");
		openHouseTalk1.setEventPicture("http://memento.epfl.ch/image/1122/112x112.jpg");
		eventItems.put(openHouseTalk1.getEventId(), openHouseTalk1);

		EventItem openHouseTalk2 = new EventItem(12000002l);
		openHouseTalk2.setEventCateg(-1);
		openHouseTalk2.setEventTitle("Open House Talk 2");
		openHouseTalk2.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T110000").getTime());
		openHouseTalk2.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T120000").getTime());
		openHouseTalk2.setFullDay(false);
		openHouseTalk2.setEventPlace("BC 410");
		openHouseTalk2.setEventPicture("http://memento.epfl.ch/image/1121/112x112.jpg");
		eventItems.put(openHouseTalk2.getEventId(), openHouseTalk2);

		EventItem openHouseTalk3 = new EventItem(12000003l);
		openHouseTalk3.setEventCateg(-1);
		openHouseTalk3.setEventTitle("Open House Talk 3");
		openHouseTalk3.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T120000").getTime());
		openHouseTalk3.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T130000").getTime());
		openHouseTalk3.setFullDay(false);
		openHouseTalk3.setEventPlace("BC 410");
		openHouseTalk3.setEventPicture("http://memento.epfl.ch/image/1124/112x112.jpg");
		eventItems.put(openHouseTalk3.getEventId(), openHouseTalk3);

		EventItem openHouseTalk4 = new EventItem(12000004l);
		openHouseTalk4.setEventCateg(-1);
		openHouseTalk4.setEventTitle("Open House Talk 4");
		openHouseTalk4.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T130000").getTime());
		openHouseTalk4.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse("20130322T140000").getTime());
		openHouseTalk4.setFullDay(false);
		openHouseTalk4.setEventPlace("BC 410");
		openHouseTalk4.setEventPicture("http://memento.epfl.ch/image/1125/112x112.jpg");
		eventItems.put(openHouseTalk4.getEventId(), openHouseTalk4);

	}*/
	
	/*@Override
	public EventPoolChildrenReply getEventPoolChildren(EventPoolChildrenRequest iEventsRequest) throws TException {
		System.out.println("EventPoolChildrenReply");
		
		//String lang = (iEventsRequest.isSetLang() ? iEventsRequest.getLang() : "en");
		String feed = "epfl";
		String categ = "";
		String period = "period=" + (iEventsRequest.isSetPeriod() ? iEventsRequest.getPeriod() : 1);
		try {
			Map<Long, EventItem> eventItems = new HashMap<Long, EventItem>();
			List<Long> topLevelEventsIds = new LinkedList<Long>();
			Document doc = Jsoup.connect("http://memento.epfl.ch/" + feed + "/?" + categ + period).get();
			Elements htmlItems = doc.select("div[itemscope]");
			for(Element e : htmlItems){
				//System.out.println("===============");
				EventItem ev = new EventItem();
				Elements imgs = e.getElementsByAttributeValue("itemprop", "image");
				if(imgs.size() > 0) {
					ev.setEventPicture(imgs.get(0).attr("src"));
					//System.out.println("IMG: " + imgs.get(0).attr("src"));
				}
				//ev.setEventPicture(ballouta[new Random().nextInt(ballouta.length)]);
				Elements hrefs = e.getElementsByAttributeValueContaining("href", "/event/");
				for(Element h: hrefs) {
					if(h.attr("href").contains("/export/")){
						ev.setEventId(Long.parseLong(getSubstringBetween(h.attr("href"), "export/","/")));
						//System.out.println("ID: " + getSubstringBetween(h.attr("href"), "export/","/"));
					}else{
						ev.setEventUri(getSubstringBetween(h.attr("href"), "event/","/"));
						//System.out.println("URI: " + getSubstringBetween(h.attr("href"), "event/","/"));
					}
				}
				HttpURLConnection conn = (HttpURLConnection) new URL("http://memento.epfl.ch/event/export/" + ev.getEventId()).openConnection();
				//String vcal = IOUtils.toString(conn.getInputStream(), "UTF-8");
				CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
				CalendarBuilder builder = new CalendarBuilder();
				Calendar calendar = builder.build(conn.getInputStream());
				Iterator<?> i = calendar.getComponents().iterator();
				if(i.hasNext()) {
					Component component = (Component) i.next();
					//System.out.println("Component [" + component.getName() + "]");
					for (Iterator<?> j = component.getProperties().iterator(); j.hasNext();) {
						Property property = (Property) j.next();
						if(Property.CATEGORIES.equals(property.getName()))
							ev.setEventCateg(getCategFromName(property.getValue()));
						else if(Property.DESCRIPTION.equals(property.getName()))
							ev.setEventDetails(property.getValue());
						else if(Property.DTSTART.equals(property.getName()))
							ev.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
						else if(Property.DTEND.equals(property.getName()))
							ev.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
						else if(Property.LOCATION.equals(property.getName()))
							ev.setEventPlace(property.getValue());
						else if(Property.SUMMARY.equals(property.getName()))
							ev.setEventTitle(property.getValue());
						else if(Property.UID.equals(property.getName()))
							ev.setVcalUid(property.getValue());
						//System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
					}
				}
				// openhouse 10275
				//           edic-open-house-2013
				ev.setFullDay("00:00".equals(new SimpleDateFormat("HH:mm").format(new Date(ev.getStartDate()))));
				// The two attributes below should be gathered from the URI page
				//ev.setEventSpeaker(""); // TODOx
				//ev.setBroadcastInFeeds(null); // TODOx
				//ev.setParentEvent(0);
				//ev.setChildrenEvents(null);
				eventItems.put(ev.getEventId(), ev);
				topLevelEventsIds.add(ev.getEventId());
			}
			topLevelEventsIds.add(12000000l);
			
			EventItem containerEvent = new EventItem(Constants.CONTAINER_EVENT_ID);
			containerEvent.setChildrenPools(topLevelEventsIds);
			eventItems.put(containerEvent.getEventId(), containerEvent);
			addFeaturedEvents(eventItems);
			System.out.println("returned " + eventItems.size() + " events");
			
			EventPoolChildrenReply reply = new EventPoolChildrenReply(eventItems);
			reply.setCategs(Constants.EVENTS_CATEGS);
			reply.setFeeds(Constants.EVENTS_FEEDS);
			return reply;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getEvents from upstream server");
		} catch (ParserException e) {
			e.printStackTrace();
			throw new TException("Failed to parse vCal");
		} catch (ParseException e) {
			e.printStackTrace();
			throw new TException("Failed to parse vCal date");
		}
	}*/


	/**
	 * HELPER FUNCTIONS
	 */
	
	private synchronized void importFromMemento() {
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
		if(!dateLastImport.equals(date)) {
			dateLastImport = date;
			new Thread(importer).start();
		}
	}
	
	public static String getResizedPhotoUrl (String image, int newSize) {
		if(image == null)
			return null;
		if(image.contains("memento.epfl.ch/image")) {
			image = getSubstringBetween(image, "image/", "/"); // get the image id
			image = "http://memento.epfl.ch/image/" + image + "/" + newSize + "x" + newSize+ ".jpg";
		} else if(image.contains("secure.gravatar.com")) {
			image = getSubstringBetween(image, "avatar/", "?"); // get the image id
			image = "http://secure.gravatar.com/avatar/" + image + "?s=" + newSize;
		}
		return image;
	}
	
	private static int getCategFromName(String categName) {
		if("Conferences - Seminars".equals(categName) || "Conférences - Séminaires".equals(categName)) return 1;
		if("Meetings management tips".equals(categName) || "Assemblées Conseils Direction".equals(categName)) return 2;
		if("Miscellaneous".equals(categName) || "Divers".equals(categName)) return 4;
		if("Exhibitions".equals(categName) || "Expositions".equals(categName)) return 5;
		if("Movies".equals(categName) || "Films".equals(categName)) return 6;
		if("Celebrations".equals(categName) || "Fêtes".equals(categName)) return 7;
		if("Inaugural lessons - Lessons of honor".equals(categName) || "Leçons inaugurales - Leçons d'honneur".equals(categName)) return 8;
		if("Cultural events".equals(categName) || "Manifestations culturelles".equals(categName)) return 9;
		if("Sporting events".equals(categName) || "Manifestations sportives".equals(categName)) return 10;
		if("Dating EPFL - economy".equals(categName) || "Rencontres EPFL – économie".equals(categName)) return 11;
		if("Thesis defenses".equals(categName) || "Soutenances de thèses".equals(categName)) return 12;
		if("Academic calendar".equals(categName) || "Calendrier Académique".equals(categName)) return 13;
		return Integer.MAX_VALUE;
	}

	private static String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}
	
	public static <T> String join(Collection<T> coll, String separator) {
		if(coll == null)
			return null;
		StringBuilder sb = new StringBuilder();
		boolean looped = false;
		for(T t : coll) {
			if(looped)
				sb.append(separator);
			sb.append(t.toString());
			looped = true;
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static List<String> split(String blob, String regex) {
		if(blob == null)
			return null;
		return Arrays.asList(blob.split(regex));
	}
	
	private static List<String> scanForUris(String feed, int aCateg, int aPeriod) throws IOException {
		String categ = (aCateg != 0 ? ("category=" + aCateg + "&") : ""); // 0 => All
		String period = "period=" + aPeriod;
		List<String> eventsIds = new LinkedList<String>();
		Document doc = Jsoup.connect("http://memento.epfl.ch/" + feed + "/?" + categ + period).get();
		for(Element e : doc.select("div[itemscope]")){
			Elements hrefs = e.getElementsByAttributeValueContaining("href", "/event/");
			for(Element h: hrefs) {
				if(!h.attr("href").contains("/export/")){
					eventsIds.add(getSubstringBetween(h.attr("href"), "event/","/"));
					break;
				}
			}
		}
		System.out.println("feed=" + feed + " categ=" + aCateg + " period=" + aPeriod + " => " + eventsIds.size() + " events");
		return eventsIds;
	}
	
	private static Set<String> collectUris(Connection conn) throws SQLException {
		Set<String> eventsIds = new HashSet<String>();
		for(String feed : getTagsFromDb(conn, true).keySet()) {
			try {
				eventsIds.addAll(scanForUris(feed, 0, 365)); // All categories - One year
			} catch (IOException e) {
				System.out.println("AIE Exception, skipping feed=" + feed + "... " + e.getMessage());
			}
		}
		System.out.println("all-in-all " + eventsIds.size() + " events");
		return eventsIds;
	}
	
	private static EventItem parseEvent(String eventUri, Map<String, String> feedsMapToUpdate) throws IOException {
		System.out.println("Parsing event " + eventUri);
		Document doc = Jsoup.connect("http://memento.epfl.ch/event/" + eventUri + "/").get();
		Elements eventElement = doc.getElementsByAttributeValue("itemprop", "events");
		if(eventElement.size() == 0)
			return null;
		Element e = eventElement.get(0);
		EventItem ev = new EventItem();
		ev.setEventUri(eventUri);
		Elements tmp;
		tmp = e.getElementsByAttributeValue("itemprop", "image");
		if(tmp.size() > 0)
			ev.setEventPicture(tmp.get(0).attr("src"));
		tmp = e.getElementsByAttributeValueContaining("href", "/export/");
		if(tmp.size() > 0)
			ev.setEventId(Long.parseLong(getSubstringBetween(tmp.get(0).attr("href"), "export/","/")));
		tmp = e.getElementsByAttributeValue("class", "full-description");
		if(tmp.size() > 0)
			ev.setEventDetails(tmp.get(0).html());
		tmp = e.getElementsByAttributeValue("class", "location");
		if(tmp.size() > 0 && tmp.get(0).text().length() > 0) {
			ev.setEventPlace(tmp.get(0).text());
			if(tmp.get(0).hasAttr("href"))
				ev.setLocationHref(convertMapUrl(tmp.get(0).attr("href")));
		}
		tmp = e.getElementsByAttributeValue("class", "title");
		if(tmp.size() > 0)
			ev.setEventTitle(tmp.get(0).text());
		tmp = e.getElementsByAttributeValue("class", "speaker");
		if(tmp.size() > 0)
			ev.setEventSpeaker(removeFirstStrong(tmp.get(0)));
		tmp = e.getElementsByAttributeValue("class", "link");
		if(tmp.size() > 0) {
			Elements t = tmp.get(0).getElementsByTag("a");
			if(t.size() > 0)
				ev.setDetailsLink(t.get(0).attr("href"));
		}
		Elements broadcastIn = doc.getElementsByAttributeValueContaining("class", "broadcasted-in");
		if(broadcastIn.size() > 0) {
			List<String> inFeeds = new LinkedList<String>();
			for(Element t : broadcastIn.get(0).getElementsByAttributeValue("class", "showall")) {
				String feedUrl = getSubstringBetween(t.attr("href"), "/", "/");
				inFeeds.add(feedUrl);
				tmp = t.getElementsByAttributeValue("class", "label");
				if(tmp.size() > 0 && feedsMapToUpdate != null && !feedsMapToUpdate.containsKey(feedUrl))
					feedsMapToUpdate.put(feedUrl, tmp.get(0).text());
			}
			ev.setEventTags(inFeeds);
		}
		Elements categ = doc.getElementsByAttributeValueContaining("class", "category");
		if(categ.size() > 0)
			ev.setEventCateg(getCategFromName(categ.get(0).text()));
		return ev;
	}
	
	private static String convertMapUrl(String mapUrl) {
		try {
			URL url = new URL(mapUrl);
			if("plan.epfl.ch".equals(url.getHost())) {
				String qStr = url.getQuery();
				if(qStr != null) {
					String[] params = qStr.split("&");
					for(String p : params) {
						String[] param = p.split("=");
						if(param.length == 2 && ("room".equalsIgnoreCase(param[0]) || "q".equalsIgnoreCase(param[0]))) {
							return "pocketcampus://map.plugin.pocketcampus.org/search?q=" + param[1];
						}
					}
					
				}
			}
		} catch (MalformedURLException e) {
		}
		return null;
	}
	
	private static String removeFirstStrong(Element t) {
		for(Element e : t.getElementsByTag("strong")) {
			e.html("");
			break;
		}
		return t.text().trim();
	}
	
	private static void readFromIcs(EventItem ev) throws IOException, ParserException, ParseException {
		System.out.println("Reading additional info from ICS " + ev.getEventId());
		HttpURLConnection conn = (HttpURLConnection) new URL("http://memento.epfl.ch/event/export/" + ev.getEventId()).openConnection();
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(conn.getInputStream());
		Iterator<?> i = calendar.getComponents().iterator();
		if(i.hasNext()) {
			Component component = (Component) i.next();
			for (Iterator<?> j = component.getProperties().iterator(); j.hasNext();) {
				Property property = (Property) j.next();
				if(Property.DTSTART.equals(property.getName()))
					ev.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
				else if(Property.DTEND.equals(property.getName()))
					ev.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
				else if(Property.UID.equals(property.getName()))
					ev.setVcalUid(property.getValue());
			}
		}
		if(ev.isSetStartDate())
			ev.setFullDay("00:00".equals(new SimpleDateFormat("HH:mm").format(new Date(ev.getStartDate()))));
	}

	private static void updateEventItem(EventItem ei, Connection conn) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("REPLACE INTO eventitems (eventId,startDate,endDate,fullDay,eventThumbnail,eventTitle,eventPlace,eventSpeaker,eventDetails,parentPool,eventUri,vcalUid,eventCateg,broadcastInFeeds,locationHref,detailsLink) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		stm.setLong(1, ei.getEventId());
		stm.setTimestamp(2, (ei.isSetStartDate() ? new Timestamp(ei.getStartDate()) : null));
		stm.setTimestamp(3, (ei.isSetEndDate() ? new Timestamp(ei.getEndDate()) : null));
		stm.setBoolean(4, ei.isFullDay());
		stm.setString(5, ei.getEventPicture());
		stm.setString(6, ei.getEventTitle());
		stm.setString(7, ei.getEventPlace());
		stm.setString(8, ei.getEventSpeaker());
		stm.setString(9, ei.getEventDetails());
		stm.setLong(10, Constants.CONTAINER_EVENT_ID);
		stm.setString(11, ei.getEventUri());
		stm.setString(12, ei.getVcalUid());
		if(ei.isSetEventCateg()) stm.setInt(13, ei.getEventCateg());
		else stm.setNull(13, Types.INTEGER);
		stm.setString(14, join(ei.getEventTags(), ","));
		stm.setString(15, ei.getLocationHref());
		stm.setString(16, ei.getDetailsLink());
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated event " + ei.getEventId());
	}
	
	private static void updateEventTag(String key, String value, Connection conn, boolean isMemento) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("REPLACE INTO eventtags(feedKey,feedValue,isMemento) VALUES (?,?," + (isMemento ? "1" : "NULL" ) + ");");
		stm.setString(1, key);
		stm.setString(2, value);
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated feed " + key);
	}
	
	private static Map<String, String> getTagsFromDb(Connection conn, boolean mementoOnly) throws SQLException {
		Map<String, String> feeds = new HashMap<String, String>();
		PreparedStatement stm = conn.prepareStatement("SELECT feedKey,feedValue FROM eventtags " + (mementoOnly ? "WHERE isMemento=1" : ""));
		ResultSet rs = stm.executeQuery();
		while(rs.next()) {
			feeds.put(rs.getString(1), rs.getString(2));
		}
		rs.close();
		stm.close();
		return feeds;
	}
	
	private static Map<Integer, String> getCategsFromDb(Connection conn) throws SQLException {
		Map<Integer, String> categs = new HashMap<Integer, String>();
		PreparedStatement stm = conn.prepareStatement("SELECT categKey,categValue FROM eventcategs;");
		ResultSet rs = stm.executeQuery();
		while(rs.next()) {
			categs.put(rs.getInt(1), rs.getString(2));
		}
		rs.close();
		stm.close();
		return categs;
	}
	
	private static class MyQuery {
		StringBuilder query = new StringBuilder();
		List<Object> values = new LinkedList<Object>();
		MyQuery addPart(String part) {
			query.append(part);
			return this;
		}
		MyQuery addPartWithValue(String part, Object arg) {
			query.append(part);
			values.add(arg);
			return this;
		}
		MyQuery addPartWithList(String part, List<? extends Object> arg, String partIfEmptyList) {
			if(arg.size() == 0)
				return addPart(partIfEmptyList);
			List<String> placeholders = new LinkedList<String>();
			for(Object o : arg){
				values.add(o);
				placeholders.add("?");
			}
			query.append(part.replace("?", "(" + join(placeholders, ", ") + ")"));
			return this;
		}
		String getQuery() {
			return query.toString();
		}
		PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
			PreparedStatement stm = conn.prepareStatement(getQuery());
			int index = 1;
			for(Object arg : values) {
				if(arg instanceof Integer)
					stm.setInt(index++, ((Integer) arg));
				else if(arg instanceof Long)
					stm.setLong(index++, ((Long) arg));
				else if(arg instanceof String)
					stm.setString(index++, ((String) arg));
				else if(arg instanceof Boolean)
					stm.setBoolean(index++, ((Boolean) arg));
				else
					throw new RuntimeException("Hey, are you kidding me? What kind of obj is that?");
			}
			return stm;
		}
	};
	
	private static class EventItemDecoderFromDb {
		private static final String EVENTITEMS_SELECT_FIELDS = "eventId,startDate,endDate,fullDay,eventPicture,eventTitle,eventPlace,eventSpeaker,eventDetails,parentPool,eventUri,vcalUid,eventCateg,broadcastInFeeds,locationHref,detailsLink,secondLine,timeSnippet,hideEventInfo,hideTitle,eventThumbnail,hideThumbnail";
		private static EventItem decodeFromResultSet(ResultSet rs, int level) throws SQLException {
			EventItem ei = new EventItem();
			
			ei.setEventId(rs.getLong(1));
			if(rs.wasNull()) ei.unsetEventId(); // should never happen
			
			ei.setEventCateg(rs.getInt(13));
			if(rs.wasNull()) ei.unsetEventCateg();
			
			ei.setChildrenPools(new LinkedList<Long>());
			ei.setParentPool(rs.getLong(10));
			
			if(level < 10)
				return ei;
			
			ei.setEventTitle(rs.getString(6));
			ei.setEventThumbnail("http://pocketcampus.epfl.ch/images/padlock.png");
			ei.setSecondLine("Use the scan button to exchange contact information with this person");
			
			if(level < 100)
				return ei;
			
			Date startDate = rs.getTimestamp(2);
			if(startDate != null) ei.setStartDate(startDate.getTime());
			Date endDate = rs.getTimestamp(3);
			if(endDate != null) ei.setEndDate(endDate.getTime());
			ei.setFullDay(rs.getBoolean(4));
			if(rs.wasNull()) ei.unsetFullDay();
			
			ei.setEventPicture(getResizedPhotoUrl(rs.getString(5), 500));
			ei.setEventThumbnail(getResizedPhotoUrl(rs.getString(21), 100));
			ei.setEventTitle(rs.getString(6));
			ei.setEventPlace(rs.getString(7));
			ei.setEventSpeaker(rs.getString(8));
			ei.setEventDetails(rs.getString(9));
			ei.setEventUri(rs.getString(11));
			ei.setVcalUid(rs.getString(12));
			
			ei.setEventTags(split(rs.getString(14), "[,]"));
			ei.setLocationHref(rs.getString(15));
			ei.setDetailsLink(rs.getString(16));
			ei.setSecondLine(rs.getString(17));
			ei.setTimeSnippet(rs.getString(18));
			ei.setHideEventInfo(rs.getBoolean(19));
			ei.setHideTitle(rs.getBoolean(20));
			ei.setHideThumbnail(rs.getBoolean(22));
			
			return ei;
		}
		private static void makeSelfEvent(EventItem ei, String exchangeToken) {
			// force categ to Me
			ei.setEventCateg(-3);
			// force big picture to QR-code 
			ei.setEventPicture("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=" + ei.getParentPool() + "%26markFavorite=" + ei.getEventId());
			// remove details
			ei.setEventDetails(null);
			// show help
			ei.setSecondLine("Allow others to scan your barcode to exchange contact information with them");
		}
		public static MyQuery getSelectPublicEventItemsQuery() {
			return new MyQuery().
					addPart("SELECT " + EVENTITEMS_SELECT_FIELDS + ",userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems LEFT JOIN eventusers ON eventId=mappedEvent WHERE (isProtected IS NULL)");
		}
		public static MyQuery getSelectAccessibleEventItemsQuery(List<String> token) {
			return new MyQuery().
					addPart("SELECT " + EVENTITEMS_SELECT_FIELDS + ",permLevel AS PERM_LEVEL,userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems INNER JOIN eventperms ON eventItemId=eventId LEFT JOIN eventusers ON eventId=mappedEvent").
					addPartWithList(" WHERE (userToken IN ?)", token, " WHERE (1=0)");
		}
		public static MyQuery getFillChildrenEventPoolsQuery() {
			return new MyQuery().
					addPart("SELECT eventId,poolId FROM eventitems,eventpools WHERE (eventId=parentEvent)");
		}
		public static Map<Long, EventItem> getEventItemsUsingQueries(Connection conn, MyQuery publicItemsQuery, MyQuery accessibleItemsQuery, MyQuery fillChildrenQuery, List<String> token) throws SQLException {
			PreparedStatement stm;
			ResultSet rs;
			Map<Long, EventItem> items = new HashMap<Long, EventItem>();
			
			stm = publicItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while(rs.next()) {
				EventItem ei = decodeFromResultSet(rs, 100);
				if(token.contains(rs.getString("USER_ID")))
					makeSelfEvent(ei, rs.getString("EXCHANGE_TOKEN"));
				items.put(ei.getEventId(), ei);
			}
			rs.close();
			stm.close();
			
			stm = accessibleItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while(rs.next()) {
				if(rs.getInt("PERM_LEVEL") == 0)
					continue;
				EventItem ei = decodeFromResultSet(rs, rs.getInt("PERM_LEVEL"));
				if(token.contains(rs.getString("USER_ID")))
					makeSelfEvent(ei, rs.getString("EXCHANGE_TOKEN"));
				items.put(ei.getEventId(), ei);
			}
			rs.close();
			stm.close();
			
			// Now fill children
			stm = fillChildrenQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while(rs.next()) {
				if(items.get(rs.getLong(1)) != null)
					items.get(rs.getLong(1)).getChildrenPools().add(rs.getLong(2));
			}
			rs.close();
			stm.close();

			return items;
		}
	}
	
	private static Map<Long, EventItem> eventItemsFromDb(Connection conn, long parentId, int period, List<String> token) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery().
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token).
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		if(period > 0) {
			String timeConstraint = " AND (DATEDIFF(startDate,NOW())<? AND DATEDIFF(endDate,NOW())>=0)";
			publicEvents.addPartWithValue(timeConstraint, new Integer(period));
			accessibleEvents.addPartWithValue(timeConstraint, new Integer(period));
			fillChildren.addPartWithValue(timeConstraint, new Integer(period));
		} else if(period < 0) {
			String timeConstraint = " AND (DATEDIFF(endDate,NOW())<0 AND DATEDIFF(endDate,NOW())>=?)";
			publicEvents.addPartWithValue(timeConstraint, new Integer(period));
			accessibleEvents.addPartWithValue(timeConstraint, new Integer(period));
			fillChildren.addPartWithValue(timeConstraint, new Integer(period));
		}
		return EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
	}
	
	private static EventItem eventItemFromDb(Connection conn, long id, List<String> token) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery().
				addPartWithValue(" AND (eventId=?)", new Long(id));
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token).
				addPartWithValue(" AND (eventId=?)", new Long(id));
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithValue(" AND (eventId=?)", new Long(id));
		Map<Long, EventItem> res = EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
		if(res.size() == 0)
			return null;
		return res.get(res.keySet().iterator().next());
	}
	
	private static Map<Long, EventItem> eventItemsByIds(Connection conn, List<Long> ids, List<String> token) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery().
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token).
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		return EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
	}
	
	private static class EventPoolDecoderFromDb {
		private static final String EVENTPOOLS_SELECT_FIELDS = "poolId,poolPicture,poolTitle,poolPlace,poolDetails,disableStar,disableFilterByCateg,disableFilterByTags,enableScan,refreshOnBack,sendStarred,noResultText,overrideLink,parentEvent";
		private static EventPool decodeFromResultSet(ResultSet rs) throws SQLException {
			EventPool ep = new EventPool();
			
			ep.setPoolId(rs.getLong(1));
			if(rs.wasNull()) ep.unsetPoolId(); // should never happen
			
			ep.setPoolPicture(getResizedPhotoUrl(rs.getString(2), 50));
			ep.setPoolTitle(rs.getString(3));
			ep.setPoolPlace(rs.getString(4));
			ep.setPoolDetails(rs.getString(5));
			ep.setDisableStar(rs.getBoolean(6));
			ep.setDisableFilterByCateg(rs.getBoolean(7));
			ep.setDisableFilterByTags(rs.getBoolean(8));
			ep.setEnableScan(rs.getBoolean(9));
			ep.setRefreshOnBack(rs.getBoolean(10));
			ep.setSendStarredItems(rs.getBoolean(11));
			ep.setNoResultText(rs.getString(12));
			ep.setOverrideLink(rs.getString(13));
			ep.setParentEvent(rs.getLong(14));
			ep.setChildrenEvents(new LinkedList<Long>());
			
			return ep;
		}
		public static MyQuery getSelectEventPoolsQuery() {
			return new MyQuery().
					addPart("SELECT " + EVENTPOOLS_SELECT_FIELDS + " FROM eventpools");
		}
		public static MyQuery getFillChildrenEventItemsQuery() {
			return new MyQuery().
					addPart("SELECT poolId,eventId FROM eventpools,eventitems WHERE (parentPool=poolId)");
		}
		public static Map<Long, EventPool> getEventPoolsUsingQueries(Connection conn, MyQuery selectPoolsQuery, MyQuery fillChildrenItemsQuery) throws SQLException {
			PreparedStatement stm;
			ResultSet rs;
			Map<Long, EventPool> pools = new HashMap<Long, EventPool>();
			
			stm = selectPoolsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while(rs.next()) {
				EventPool ep = decodeFromResultSet(rs);
				pools.put(ep.getPoolId(), ep);
			}
			rs.close();
			stm.close();
			
			// Now fill children
			stm = fillChildrenItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while(rs.next()) {
				if(pools.get(rs.getLong(1)) != null)
					pools.get(rs.getLong(1)).getChildrenEvents().add(rs.getLong(2));
			}
			rs.close();
			stm.close();

			return pools;
		}
	}
	
	private static Map<Long, EventPool> eventPoolsFromDb(Connection conn, long parentId) throws SQLException {
		MyQuery publicEvents = EventPoolDecoderFromDb.getSelectEventPoolsQuery().
				addPartWithValue(" WHERE (parentEvent=?)", new Long(parentId));
		MyQuery fillChildren = EventPoolDecoderFromDb.getFillChildrenEventItemsQuery().
				addPartWithValue(" AND (parentEvent=?)", new Long(parentId));
		return EventPoolDecoderFromDb.getEventPoolsUsingQueries(conn, publicEvents, fillChildren);
	}
	
	private static EventPool eventPoolFromDb(Connection conn, long id) throws SQLException {
		MyQuery publicEvents = EventPoolDecoderFromDb.getSelectEventPoolsQuery().
				addPartWithValue(" WHERE (poolId=?)", new Long(id));
		MyQuery fillChildren = EventPoolDecoderFromDb.getFillChildrenEventItemsQuery().
				addPartWithValue(" AND (poolId=?)", new Long(id));
		Map<Long, EventPool> res = EventPoolDecoderFromDb.getEventPoolsUsingQueries(conn, publicEvents, fillChildren);
		if(res.size() == 0)
			return null;
		return res.get(res.keySet().iterator().next());
	}

	private static List<Long> filterStarred(Connection conn, List<Long> starred, long parentEvent) throws SQLException {
		List<Long> filtered = new LinkedList<Long>();
		for(Long l : starred) {
			if(isSubeventOf(conn, l, parentEvent, new HashSet<Long>()))
				filtered.add(l);
		}
		return filtered;
	}
	
	private static boolean isSubeventOf(Connection conn, long event, long parent, Set<Long> history) throws SQLException {
		if(event == parent)
			return true;
		event = getGrandParentEventItemId(conn, event);
		if(history.contains(event))
			return false; // get rid of loops
		history.add(event);
		if(history.size() > 20)
			return false; // limit recursion depth (screw it if it is more than 20)
		if(event == 0)
			return false;
		return isSubeventOf(conn, event, parent, history);
	}

	private static long getGrandParentEventItemId(Connection conn, long id) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		long grandParent = 0;
		stm = conn.prepareStatement("SELECT parentEvent FROM eventitems INNER JOIN eventpools ON parentPool = poolId WHERE eventId = ?;");
		stm.setLong(1, id);
		rs = stm.executeQuery();
		if(rs.next()) {
			grandParent = rs.getLong(1);
		}
		rs.close();
		stm.close();
		
		return grandParent;
	}

	private static String userTokenFromExchangeToken(Connection conn, String exchangeToken) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		String userToken = null;
		stm = conn.prepareStatement("SELECT userId FROM eventusers WHERE exchangeToken=?;");
		stm.setString(1, exchangeToken);
		rs = stm.executeQuery();
		if(rs.next()) {
			userToken = rs.getString(1);
		}
		rs.close();
		stm.close();
		
		return userToken;
	}

	private static int exchangeContacts(Connection conn, String userToken1, String userToken2) throws SQLException {
		PreparedStatement stm;
		int affectedRows = 0;
		String query = "UPDATE eventperms SET permLevel=100 WHERE userToken=? AND eventItemId=(SELECT mappedEvent FROM eventusers WHERE userId=?);";
		
		stm = conn.prepareStatement(query);
		stm.setString(1, userToken1);
		stm.setString(2, userToken2);
		affectedRows += stm.executeUpdate();
		stm.close();
		
		stm = conn.prepareStatement(query);
		stm.setString(1, userToken2);
		stm.setString(2, userToken1);
		affectedRows += stm.executeUpdate();
		stm.close();
		
		return affectedRows;
	}

	private static void fixCategAndTags(EventItem e) {
		if(!e.isSetEventCateg())
			e.setEventCateg(1000000); // uncategorized
		if(!e.isSetEventTags() || e.getEventTags().size() == 0)
			e.setEventTags(oneItemList("unlabeled")); // unlabeled
	}
	
	private static <T> List<T> oneItemList(T obj) {
		List<T> list = new LinkedList<T>();
		list.add(obj);
		return list;
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> arrayToList(T[] a) {
		return Arrays.asList(a);
	}
	
	private static class GmailSender {

		public static boolean sendEmail(String to, String subject, String htmlBody) {
			final String username = PC_SRV_CONFIG.getString("BOT_EMAIL_ACCOUNT_USERNAME");
			final String password = PC_SRV_CONFIG.getString("BOT_EMAIL_ACCOUNT_PASSWORD");
			if(username == null || password == null)
				return false;

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("noreply@pocketcampus.org", "PocketCampus"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				message.setSubject(subject);
				//message.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!");
				
				// Create a multi-part to combine the parts
				Multipart multipart = new MimeMultipart("alternative");
				// Create your text message part
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("Your browser does not support the format of this email. Please open it in a browser that supports HTML.");
				// Add the text part to the multipart
				multipart.addBodyPart(messageBodyPart);
				// Create the html part
				messageBodyPart = new MimeBodyPart();
				String htmlMessage = htmlBody;
				messageBodyPart.setContent(htmlMessage, "text/html");
				// Add html part to multi part
				multipart.addBodyPart(messageBodyPart);
				// Associate multi-part with message
				message.setContent(multipart);
				
				Transport.send(message);
				return true;
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	private static class SendEmailInfo {
		private String emailAddress;
		private String userToken;
		private String addressingName;
		public SendEmailInfo(String ea, String ut, String an) { emailAddress = ea; userToken = ut; addressingName = an; }
		public String getEmailAddress() { return emailAddress; }
		public String getUserToken() { return userToken; }
		public String getAddressingName() { return addressingName; }
	}

	private static class EmailTemplateInfo {
		private long participantsPool;
		private String emailTitle;
		private String emailBody;
		private List<String> sendOnlyTo;
		public EmailTemplateInfo(long pp, String et, String eb, List<String> sot) { participantsPool = pp; emailTitle = et; emailBody = eb; sendOnlyTo = sot; }
		public long getParticipantsPool() { return participantsPool; }
		public String getEmailTitle() { return emailTitle; }
		public String getEmailBody() { return emailBody; }
		public List<String> getSendOnlyTo() { return sendOnlyTo; }
	}

}
