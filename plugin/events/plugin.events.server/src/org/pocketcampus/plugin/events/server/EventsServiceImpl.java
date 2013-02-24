package org.pocketcampus.plugin.events.server;

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
import java.util.Collection;
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

import org.apache.thrift.TException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventItemReply;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventPool;
import org.pocketcampus.plugin.events.shared.EventPoolReply;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService;

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
		// TODO int a = 17301535;
		System.out.println("getEventItemChildren");
		long parentId = req.getEventItemId();
		try {
			Connection conn = connMgr.getConnection();
			EventItem item = eventItemFromDb(conn, parentId, (req.isSetUserToken() ? req.getUserToken() : ""));
			Map<Long, EventPool> childrenPools = eventPoolsFromDb(conn, parentId);
			item.setChildrenPools(new LinkedList<Long>(childrenPools.keySet()));
			EventItemReply reply = new EventItemReply(200);
			reply.setEventItem(item);
			reply.setChildrenPools(childrenPools);
			reply.setTags(getTagsFromDb(conn));
			reply.setCategs(getCategsFromDb(conn));
			conn.close();
			System.out.println("returned " + reply.getChildrenPools().size() + " pools");
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventItemReply(500);
		}
	}
	
	@Override
	public EventPoolReply getEventPool(EventPoolRequest req) throws TException {
		System.out.println("getEventPoolChildren");
		long parentId = req.getEventPoolId();
		int period = (req.isSetPeriod() ? req.getPeriod() : 1);
		try {
			Connection conn = connMgr.getConnection();
			EventPool pool = eventPoolFromDb(conn, parentId);
			Map<Long, EventItem> childrenItems = eventItemsFromDb(conn, parentId, period, (req.isSetUserToken() ? req.getUserToken() : ""));
			pool.setChildrenEvents(new LinkedList<Long>(childrenItems.keySet()));
			EventPoolReply reply = new EventPoolReply(200);
			reply.setEventPool(pool);
			reply.setChildrenItems(childrenItems);
			reply.setTags(getTagsFromDb(conn));
			reply.setCategs(getCategsFromDb(conn));
			conn.close();
			System.out.println("returned " + reply.getChildrenItems().size() + " items");
			return reply;
		} catch (SQLException e) {
			e.printStackTrace();
			return new EventPoolReply(500);
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

	@Override
	public String updateDatabase(String arg) throws TException {
		System.out.println("updateDatabase");
		try {
			HashMap<String, String> feedsMap = new HashMap<String, String>();
			Connection conn = connMgr.getConnection();
			for(String uri : collectUris(conn)) {
				EventItem ei = parseEvent(uri, feedsMap);
				readFromIcs(ei);
				updateEventItem(ei, conn);
			}
			for(String key : feedsMap.keySet()) {
				updateEventTag(key, feedsMap.get(key), conn);
			}
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * HELPER FUNCTIONS
	 */
	
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
	
	private static Set<String> collectUris(Connection conn) throws IOException, SQLException {
		Set<String> eventsIds = new HashSet<String>();
		for(String feed : getTagsFromDb(conn).keySet()) {
			eventsIds.addAll(scanForUris(feed, 0, 365)); // All categories - One year
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
				ev.setLocationHref(tmp.get(0).attr("href"));
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
		PreparedStatement stm = conn.prepareStatement("REPLACE INTO eventitems (eventId,startDate,endDate,fullDay,eventPicture,eventTitle,eventPlace,eventSpeaker,eventDetails,parentPool,eventUri,vcalUid,eventCateg,broadcastInFeeds,locationHref,detailsLink) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
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
	
	private static void updateEventTag(String key, String value, Connection conn) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("REPLACE INTO eventtags(feedKey,feedValue) VALUES (?,?);");
		stm.setString(1, key);
		stm.setString(2, value);
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated feed " + key);
	}
	
	private static Map<String, String> getTagsFromDb(Connection conn) throws SQLException {
		Map<String, String> feeds = new HashMap<String, String>();
		PreparedStatement stm = conn.prepareStatement("SELECT feedKey,feedValue FROM eventtags;");
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
	
	private static class EventItemDecoderFromDb {
		private static final String SELECT_FIELDS = "eventId,startDate,endDate,fullDay,eventPicture,eventTitle,eventPlace,eventSpeaker,eventDetails,parentPool,eventUri,vcalUid,eventCateg,broadcastInFeeds,locationHref,detailsLink";
		public static String getSelectFields() {
			return SELECT_FIELDS;
		}
		public static EventItem decodeFromResultSet(ResultSet rs, int level, String exchangeToken) throws SQLException {
			EventItem ei = new EventItem();
			
			if(exchangeToken != null) // self-event
				level = 100; // full trust
			
			ei.setEventId(rs.getLong(1));
			if(rs.wasNull()) ei.unsetEventId(); // should never happen
			
			ei.setEventCateg(rs.getInt(13));
			if(rs.wasNull()) ei.unsetEventCateg();
			
			ei.setChildrenPools(new LinkedList<Long>());
			
			if(level < 10)
				return ei;
			
			ei.setEventPicture("drawable://17301535");
			ei.setEventTitle(rs.getString(6));
			
			if(level < 100)
				return ei;
			
			Date startDate = rs.getTimestamp(2);
			if(startDate != null) ei.setStartDate(startDate.getTime());
			Date endDate = rs.getTimestamp(3);
			if(endDate != null) ei.setEndDate(endDate.getTime());
			ei.setFullDay(rs.getBoolean(4));
			if(rs.wasNull()) ei.unsetFullDay();
			
			ei.setEventPicture(rs.getString(5));
			ei.setEventTitle(rs.getString(6));
			ei.setEventPlace(rs.getString(7));
			ei.setEventSpeaker(rs.getString(8));
			ei.setEventDetails(rs.getString(9));
			ei.setEventUri(rs.getString(11));
			ei.setVcalUid(rs.getString(12));
			
			ei.setEventTags(split(rs.getString(14), "[,]"));
			ei.setLocationHref(rs.getString(15));
			ei.setDetailsLink(rs.getString(16));
			
			if(exchangeToken != null) {
				ei.setEventCateg(-3); // force categ to Me
				//ei.setEventDetails("<p><img src=\"http://chart.apis.google.com/chart?cht=qr&chs=400x400&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?exchangeToken=" + exchangeToken + "\" style=\"width:400px;height:400px;\"></p>");
				ei.setEventPicture("http://chart.apis.google.com/chart?cht=qr&chs=400x400&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?exchangeToken=" + exchangeToken);
				ei.setEventDetails(null);
			}
			
			return ei;
		}
	}
	
	private static Map<Long, EventItem> eventItemsFromDb(Connection conn, long parentId, int period, String token) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		Map<Long, EventItem> items = new HashMap<Long, EventItem>();
		stm = conn.prepareStatement("SELECT " + EventItemDecoderFromDb.getSelectFields() + ",userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems LEFT JOIN eventusers ON eventId=mappedEvent WHERE (parentPool=?) AND (isProtected IS NULL) AND ( (DATEDIFF(startDate,NOW())<? AND DATEDIFF(endDate,NOW())>=0) OR (startDate IS NULL AND endDate IS NULL) );");
		stm.setLong(1, parentId);
		stm.setInt(2, period);
		rs = stm.executeQuery();
		while(rs.next()) {
			EventItem ei = EventItemDecoderFromDb.decodeFromResultSet(rs, 100, (token.equals(rs.getString("USER_ID")) ? rs.getString("EXCHANGE_TOKEN") : null));
			items.put(ei.getEventId(), ei);
		}
		rs.close();
		stm.close();
		
		stm = conn.prepareStatement("SELECT " + EventItemDecoderFromDb.getSelectFields() + ",permLevel AS PERM_LEVEL,userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems INNER JOIN eventperms ON eventItemId=eventId LEFT JOIN eventusers ON eventId=mappedEvent WHERE (parentPool=?) AND (userToken=?) AND ( (DATEDIFF(startDate,NOW())<? AND DATEDIFF(endDate,NOW())>=0) OR (startDate IS NULL AND endDate IS NULL) );");
		stm.setLong(1, parentId);
		stm.setString(2, token);
		stm.setInt(3, period);
		rs = stm.executeQuery();
		while(rs.next()) {
			EventItem ei = EventItemDecoderFromDb.decodeFromResultSet(rs, rs.getInt("PERM_LEVEL"), (token.equals(rs.getString("USER_ID")) ? rs.getString("EXCHANGE_TOKEN") : null));
			items.put(ei.getEventId(), ei);
		}
		rs.close();
		stm.close();
		
		// Now fill children
		stm = conn.prepareStatement("SELECT eventId,poolId FROM eventitems,eventpools WHERE (eventId=parentEvent) AND (parentPool=?) AND ( (DATEDIFF(startDate,NOW())<? AND DATEDIFF(endDate,NOW())>=0) OR (startDate IS NULL AND endDate IS NULL) );");
		stm.setLong(1, parentId);
		stm.setInt(2, period);
		rs = stm.executeQuery();
		while(rs.next()) {
			if(items.get(rs.getLong(1)) != null)
				items.get(rs.getLong(1)).getChildrenPools().add(rs.getLong(2));
		}
		rs.close();
		stm.close();

		return items;
	}
	
	private static EventItem eventItemFromDb(Connection conn, long id, String token) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		EventItem ei = null;
		stm = conn.prepareStatement("SELECT " + EventItemDecoderFromDb.getSelectFields() + ",userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems LEFT JOIN eventusers ON eventId=mappedEvent WHERE eventId=? AND isProtected IS NULL;");
		stm.setLong(1, id);
		rs = stm.executeQuery();
		if(rs.next()) {
			ei = EventItemDecoderFromDb.decodeFromResultSet(rs, 100, (token.equals(rs.getString("USER_ID")) ? rs.getString("EXCHANGE_TOKEN") : null));
		}
		rs.close();
		stm.close();
		
		if(ei != null)
			return ei;
		
		stm = conn.prepareStatement("SELECT " + EventItemDecoderFromDb.getSelectFields() + ",permLevel AS PERM_LEVEL,userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN FROM eventitems INNER JOIN eventperms ON eventItemId=eventId LEFT JOIN eventusers ON eventId=mappedEvent WHERE eventId=? AND userToken=?;");
		stm.setLong(1, id);
		stm.setString(2, token);
		rs = stm.executeQuery();
		if(rs.next()) {
			ei = EventItemDecoderFromDb.decodeFromResultSet(rs, rs.getInt("PERM_LEVEL"), (token.equals(rs.getString("USER_ID")) ? rs.getString("EXCHANGE_TOKEN") : null));
		}
		rs.close();
		stm.close();
		
		return ei;
	}
	
	private static class EventPoolDecoderFromDb {
		private static final String SELECT_FIELDS = "poolId,poolPicture,poolTitle,poolPlace,poolDetails,disableStar,parentEvent";
		public static String getSelectFields() {
			return SELECT_FIELDS;
		}
		public static EventPool decodeFromResultSet(ResultSet rs) throws SQLException {
			EventPool ep = new EventPool();
			
			ep.setPoolId(rs.getLong(1));
			if(rs.wasNull()) ep.unsetPoolId(); // should never happen
			
			ep.setPoolPicture(rs.getString(2));
			ep.setPoolTitle(rs.getString(3));
			ep.setPoolPlace(rs.getString(4));
			ep.setPoolDetails(rs.getString(5));
			ep.setDisableStar(rs.getBoolean(6));
			ep.setChildrenEvents(new LinkedList<Long>());
			
			return ep;
		}
	}
	
	private static Map<Long, EventPool> eventPoolsFromDb(Connection conn, long parentId) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		Map<Long, EventPool> pools = new HashMap<Long, EventPool>();
		stm = conn.prepareStatement("SELECT " + EventPoolDecoderFromDb.getSelectFields() + " FROM eventpools WHERE (parentEvent=?);");
		stm.setLong(1, parentId);
		rs = stm.executeQuery();
		while(rs.next()) {
			EventPool ep = EventPoolDecoderFromDb.decodeFromResultSet(rs);
			pools.put(ep.getPoolId(), ep);
		}
		rs.close();
		stm.close();
		
		// Now fill children
		stm = conn.prepareStatement("SELECT poolId,eventId FROM eventpools,eventitems WHERE (parentPool=poolId) AND (parentEvent=?);");
		stm.setLong(1, parentId);
		rs = stm.executeQuery();
		while(rs.next()) {
			if(pools.get(rs.getLong(1)) != null)
				pools.get(rs.getLong(1)).getChildrenEvents().add(rs.getLong(2));
		}
		rs.close();
		stm.close();

		return pools;
	}
	
	private static EventPool eventPoolFromDb(Connection conn, long id) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;
		
		EventPool ep = null;
		stm = conn.prepareStatement("SELECT " + EventPoolDecoderFromDb.getSelectFields() + " FROM eventpools WHERE (poolId=?);");
		stm.setLong(1, id);
		rs = stm.executeQuery();
		if(rs.next()) {
			ep = EventPoolDecoderFromDb.decodeFromResultSet(rs);
		}
		rs.close();
		stm.close();
		
		return ep;
	}

}
