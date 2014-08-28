package org.pocketcampus.plugin.events.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.events.server.utils.DBUtils;
import org.pocketcampus.plugin.events.server.utils.ImporterUtils;
import org.pocketcampus.plugin.events.server.utils.Utils;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;

public class MementoImporter {

	private static String dateLastImport = null;

	public static synchronized void importFromMemento(final ConnectionManager connMgr) {
		boolean shouldImport = new Boolean(PocketCampusServer.CONFIG.getString("IMPORT_FROM_MEMENTO"));
		if(!shouldImport) {
			return;
		}
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
		if(dateLastImport == null) {
			dateLastImport = date;
			return;
		}
		if(dateLastImport.equals(date)) {
			return;
		}
		dateLastImport = date;
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Started Async Import from Memento on " + dateLastImport);
				try {
					HashMap<String, String> feedsMap = new HashMap<String, String>();
					Connection conn = connMgr.getConnection();
					for (String uri : collectUris(conn)) {
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
					for (String key : feedsMap.keySet()) {
						updateEventTag(key, feedsMap.get(key), conn, true);
					}
					syncWithMemento(conn);
					System.out.println("Finished Async Import on " + dateLastImport);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


	private static void syncWithMemento(Connection conn) throws SQLException {
		List<Long> ids = getIdsOfMementoEventsFromDb(conn);
		System.out.println(ids.size() + " non-deleted memento events in the db");
		int deleted = 0;
		for(long i : ids) {
			try {
				new URL("http://memento.epfl.ch/event/export/" + i).openConnection().getInputStream();
			} catch (Exception e) {
				if(e instanceof FileNotFoundException) {
					markMementoEventDeleted(i, conn);
					deleted++;
				} else {
					e.printStackTrace();
				}
			}
		}
		System.out.println(deleted + " memento events were marked as deleted");
	}

	private static void updateEventTag(String key, String value, Connection conn, boolean isMemento) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("REPLACE INTO eventtags(feedKey,feedValue,isMemento) VALUES (?,?," + (isMemento ? "1" : "NULL") + ");");
		stm.setString(1, key);
		stm.setString(2, value);
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated feed " + key);
	}
	

	private static void readFromIcs(EventItem ev) throws IOException, ParserException, ParseException {
		System.out.println("Reading additional info from ICS " + ev.getEventId());
		HttpURLConnection conn = (HttpURLConnection) new URL("http://memento.epfl.ch/event/export/" + ev.getEventId()).openConnection();
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(conn.getInputStream());
		Iterator<?> i = calendar.getComponents().iterator();
		if (i.hasNext()) {
			Component component = (Component) i.next();
			for (Iterator<?> j = component.getProperties().iterator(); j.hasNext();) {
				Property property = (Property) j.next();
				if (Property.DTSTART.equals(property.getName()))
					ev.setStartDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
				else if (Property.DTEND.equals(property.getName()))
					ev.setEndDate(new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(property.getValue()).getTime());
				else if (Property.UID.equals(property.getName()))
					ev.setVcalUid(property.getValue());
			}
		}
		if (ev.isSetStartDate())
			ev.setFullDay("00:00".equals(new SimpleDateFormat("HH:mm").format(new Date(ev.getStartDate()))));
	}

	private static void updateEventItem(EventItem ei, Connection conn) throws SQLException {
		if(ei.isSetStartDate() && !ei.isSetEndDate()) // because memento decided to drop the end date; is it any useful?
			ei.setEndDate(ei.getStartDate());
		if(ei.isFullDay() && ei.isSetEndDate()) // if it's a full day, add 1 day to the end date
			ei.setEndDate(ei.getEndDate() + 24 * 3600 * 1000);
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
		if (ei.isSetEventCateg())
			stm.setInt(13, ei.getEventCateg());
		else
			stm.setNull(13, Types.INTEGER);
		stm.setString(14, Utils.join(ei.getEventTags(), ","));
		stm.setString(15, ei.getLocationHref());
		stm.setString(16, ei.getDetailsLink());
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated event " + ei.getEventId());
	}
	
	private static List<Long> getIdsOfMementoEventsFromDb(Connection conn) throws SQLException {
		List<Long> ids = new LinkedList<Long>();
		PreparedStatement stm = conn.prepareStatement("SELECT eventId FROM eventitems WHERE vcalUid IS NOT NULL AND deleted IS NULL;");
		ResultSet rs = stm.executeQuery();
		while (rs.next()) {
			ids.add(rs.getLong(1));
		}
		rs.close();
		stm.close();
		return ids;
	}
	
	private static void markMementoEventDeleted(long id, Connection conn) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("UPDATE eventitems SET deleted = 1 WHERE eventId = ?;");
		stm.setLong(1, id);
		stm.executeUpdate();
		stm.close();
	}
	

	private static List<String> scanForUris(String feed, int aCateg, int aPeriod) throws IOException {
		String categ = (aCateg != 0 ? ("category=" + aCateg + "&") : ""); // 0 => All
		String period = "period=" + aPeriod;
		List<String> eventsIds = new LinkedList<String>();
		Document doc = Jsoup.connect("http://memento.epfl.ch/" + feed + "/?" + categ + period).get();
		for (Element e : doc.select("div[itemscope]")) {
			Elements hrefs = e.getElementsByAttributeValueContaining("href", "/event/");
			for (Element h : hrefs) {
				if (!h.attr("href").contains("/export/")) {
					eventsIds.add(Utils.getSubstringBetween(h.attr("href"), "event/", "/"));
					break;
				}
			}
		}
		System.out.println("feed=" + feed + " categ=" + aCateg + " period=" + aPeriod + " => " + eventsIds.size() + " events");
		return eventsIds;
	}

	private static Set<String> collectUris(Connection conn) throws SQLException {
		Set<String> eventsIds = new HashSet<String>();
		for (String feed : DBUtils.getTagsFromDb(conn, true).keySet()) {
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
		if (eventElement.size() == 0)
			return null;
		Element e = eventElement.get(0);
		EventItem ev = new EventItem();
		ev.setEventUri(eventUri);
		Elements tmp;
		tmp = e.getElementsByAttributeValue("itemprop", "image");
		if (tmp.size() > 0)
			ev.setEventPicture(tmp.get(0).attr("src"));
		tmp = e.getElementsByAttributeValueContaining("href", "/export/");
		if (tmp.size() > 0)
			ev.setEventId(Long.parseLong(Utils.getSubstringBetween(tmp.get(0).attr("href"), "export/", "/")));
		tmp = e.getElementsByAttributeValue("class", "full-description");
		if (tmp.size() > 0)
			ev.setEventDetails(tmp.get(0).html());
		tmp = e.getElementsByAttributeValue("class", "location");
		if (tmp.size() > 0 && tmp.get(0).text().length() > 0) {
			ev.setEventPlace(tmp.get(0).text());
			if (tmp.get(0).hasAttr("href"))
				ev.setLocationHref(Utils.convertMapUrl(tmp.get(0).attr("href")));
		}
		tmp = e.getElementsByAttributeValue("class", "title");
		if (tmp.size() > 0)
			ev.setEventTitle(tmp.get(0).text());
		tmp = e.getElementsByAttributeValue("class", "speaker");
		if (tmp.size() > 0)
			ev.setEventSpeaker(Utils.removeFirstStrong(tmp.get(0)));
		tmp = e.getElementsByAttributeValue("class", "link");
		if (tmp.size() > 0) {
			Elements t = tmp.get(0).getElementsByTag("a");
			if (t.size() > 0)
				ev.setDetailsLink(t.get(0).attr("href"));
		}
		Elements broadcastIn = doc.getElementsByAttributeValueContaining("class", "broadcasted-in");
		if (broadcastIn.size() > 0) {
			List<String> inFeeds = new LinkedList<String>();
			for (Element t : broadcastIn.get(0).getElementsByAttributeValue("class", "showall")) {
				String feedUrl = Utils.getSubstringBetween(t.attr("href"), "/", "/");
				inFeeds.add(feedUrl);
				tmp = t.getElementsByAttributeValue("class", "label");
				if (tmp.size() > 0 && feedsMapToUpdate != null && !feedsMapToUpdate.containsKey(feedUrl))
					feedsMapToUpdate.put(feedUrl, tmp.get(0).text());
			}
			ev.setEventTags(inFeeds);
		}
		Elements categ = doc.getElementsByAttributeValueContaining("class", "category");
		if (categ.size() > 0)
			ev.setEventCateg(ImporterUtils.getCategFromName(categ.get(0).text()));
		return ev;
	}


	
}
