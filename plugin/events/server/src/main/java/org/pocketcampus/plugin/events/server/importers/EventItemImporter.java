package org.pocketcampus.plugin.events.server.importers;

import com.google.gson.Gson;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.events.server.utils.MyQuery;
import org.pocketcampus.plugin.events.server.utils.Utils;
import org.pocketcampus.plugin.events.shared.EventsConstants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class EventItemImporter {

	public static class MementoEvent {
		
		public String title;
		public String description;
		public String start_date; // : "2014-12-16"
		public String start_time; // : "16:15"
		public String end_date; // "2014-12-16"
		public String end_time; // "17:15"
		public String place_and_room; // : "GR A3 31"
		public String url_place_and_room; // : "http://plan.epfl.ch/?room=GR%20A3%2031"
		public String speaker; // : "xxx"
		public String organizer; // : "EESS - IIE"
		public String contact;
		public long category;
		public static class Domain{
			String label; // : "Neurosciences Brain Mind & Blue Brain";
		}
		public Domain [] domains;
		public String theme; // : ""
		public String filters; // (keywords)
		public int vulgarization; // 1: General public; 2: Informed public; 3: Experts
		public int invitation; // 1: registration required; 2: invitation required; 3: free; 
		public boolean is_internal; // : true
		public String label_link; // (more details link TEXT)
		public String url_link; // (more details link URL)
		public String image; // : "http://memento.epfl.ch/image/2644/112x112.jpg"
		public String image_description; // : ""
		
		public long id_event; // memento id: 14695
		public long id_translation; // : 23162
		public String lang; // : "en"
		public static class Memento{
			int id_memento; // : 9
			String slug; // : "sv"
		}
		public Memento [] mementos; // : Array[3]
		public String slug; // : "xxx-9"
		public String url; // : "http://memento.epfl.ch/event/xxx-9"
		public String cancel_reason;
		public boolean canceled;
		
	}
	
	public static void importEventsFromMemento(Connection conn) {
		String json = null;
		try {
			json = new HttpClientImpl().get("http://memento.epfl.ch/feeds/upcoming-events/", Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(json == null) {
			System.out.println("Events: failed to get events from Memento... aborting");
			return;
		}
		Gson gson = new Gson();
		MementoEvent [] events = gson.fromJson(json, MementoEvent[].class);
		System.out.println("Events: received " + events.length + " events from Memento");
		for(MementoEvent e : events) {
			try {
				insertUpdateEventItem(e, conn);
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("Events: failed to insert/update event " + e.slug + "... skipping");
			} catch (ParseException e2) {
				e2.printStackTrace();
				System.out.println("Events: couldn't parse date of event " + e.slug + "... skipping");
			}
		}
		System.out.println("Events: finished importing events");
	}
	
	private static void insertUpdateEventItem(MementoEvent e, Connection conn) throws SQLException, ParseException {
		fixBrokenEvent(e);
		long start, end;
		if(e.start_time == null) { // if fullDay
			start = new SimpleDateFormat("yyyy-MM-dd").parse(e.start_date).getTime();
			end = new SimpleDateFormat("yyyy-MM-dd").parse(e.end_date).getTime();
			end += 24 * 3600 * 1000;
		} else {
			start = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(e.start_date + " " + e.start_time).getTime();
			end = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(e.end_date + " " + e.end_time).getTime();
		}
		MyQuery q = new MyQuery();
		q.addPart("INSERT INTO eventitems SET ");
		addPartstoInsertQuery(q, e, start, end);
		q.addPart(" ON DUPLICATE KEY UPDATE ");
		addPartstoInsertQuery(q, e, start, end);
		PreparedStatement stm = q.getPreparedStatement(conn);
		stm.executeUpdate();
		stm.close();
		System.out.println("Events: inserted/updated event " + e.slug);
	}
	
	private static void fixBrokenEvent(MementoEvent e) {
		// if it doesn't have end time, set it to start time
		if(e.start_time != null && e.end_time == null) 
			e.end_time = e.start_time;
		// empty string means not set
		if("".equals(e.description)) e.description = null;
		if("".equals(e.place_and_room)) e.place_and_room = null;
		if("".equals(e.url_place_and_room)) e.url_place_and_room = null;
		if("".equals(e.speaker)) e.speaker = null;
		if("".equals(e.url_link)) e.url_link = null;
		if("".equals(e.title)) e.title = ("fr".equalsIgnoreCase(e.lang) ? "Sans Titre" : "No Title");
	}
	
	private static void addPartstoInsertQuery(MyQuery q, MementoEvent e, long start, long end) {
		q.addPartWithValue("`eventId` = ?,", e.id_event);
		q.addPartWithValue("`startDate` = ?,", new Timestamp(start));
		q.addPartWithValue("`endDate` = ?,", new Timestamp(end));
		q.addPartWithValue("`fullDay` = ?,", e.start_time == null ? 1 : 0);
		q.addPartWithValue("`eventPicture` = ?,", null);
		q.addPartWithValue("`eventThumbnail` = ?,", e.image);
		if("fr".equalsIgnoreCase(e.lang)) {
			q.addPartWithValue("`eventTitle_fr` = ?,", e.title);
			q.addPartWithValue("`eventDetails_fr` = ?,", e.description);
			q.addPartWithValue("`translation_fr` = ?,", e.id_translation);
		} else {
			q.addPartWithValue("`eventTitle` = ?,", e.title);
			q.addPartWithValue("`eventDetails` = ?,", e.description);
			q.addPartWithValue("`translation` = ?,", e.id_translation);
		}
		q.addPartWithValue("`eventPlace` = ?,", e.place_and_room);
		q.addPartWithValue("`locationHref` = ?,", e.url_place_and_room);
		q.addPartWithValue("`eventSpeaker` = ?,", e.speaker);
		q.addPartWithValue("`parentPool` = ?,", EventsConstants.CONTAINER_EVENT_ID);
		q.addPartWithValue("`eventUri` = ?,", e.slug);
		q.addPartWithValue("`vcalUid` = ?,", null);
		q.addPartWithValue("`eventCateg` = ?,", e.category);
		q.addPartWithValue("`broadcastInFeeds` = ?,", Utils.join(extractMementoSlugs(e), ","));
		q.addPartWithValue("`detailsLink` = ?,", e.url_link);
		q.addPartWithValue("`secondLine` = ?,", null);
		q.addPartWithValue("`timeSnippet` = ?,", null);
		q.addPartWithValue("`hideEventInfo` = ?,", null);
		q.addPartWithValue("`hideTitle` = ?,", null);
		q.addPartWithValue("`hideThumbnail` = ?,", null);
		q.addPartWithValue("`isProtected` = ?,", null);
		q.addPartWithValue("`tempDetails` = ?,", null);
		q.addPartWithValue("`deleted` = ?", e.canceled ? 1 : null);		
	}
	
	private static List<String> extractMementoSlugs(MementoEvent e) {
		List<String> broadcastInFeeds = new LinkedList<String>();
		for(MementoEvent.Memento m : e.mementos) {
			broadcastInFeeds.add(m.slug);
		}
		return broadcastInFeeds;
	}

	
}
