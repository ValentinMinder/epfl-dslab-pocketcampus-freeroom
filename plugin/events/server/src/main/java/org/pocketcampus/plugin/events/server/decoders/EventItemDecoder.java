package org.pocketcampus.plugin.events.server.decoders;

import org.pocketcampus.plugin.events.server.utils.MyQuery;
import org.pocketcampus.plugin.events.server.utils.Utils;
import org.pocketcampus.plugin.events.shared.EventItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EventItemDecoder {


	private static class EventItemDecoderFromDb {

		private static String getSelectFields(String lang) {
			String eventTitle = "CASE WHEN eventTitle is not null THEN eventTitle ELSE eventTitle_fr END AS eventTitle"; 
			String eventDetails = "CASE WHEN eventDetails is not null THEN eventDetails ELSE eventDetails_fr END AS eventDetails"; 
			if("fr".equalsIgnoreCase(lang)) {
				eventTitle = "CASE WHEN eventTitle_fr is not null THEN eventTitle_fr ELSE eventTitle END AS eventTitle_fr"; 
				eventDetails = "CASE WHEN eventDetails_fr is not null THEN eventDetails_fr ELSE eventDetails END AS eventDetails_fr"; 
			}
			return "eventId,startDate,endDate,fullDay,eventPicture," + eventTitle + ",eventPlace,eventSpeaker," + eventDetails + ",parentPool,eventUri,vcalUid,eventCateg,broadcastInFeeds,locationHref,detailsLink,secondLine,timeSnippet,hideEventInfo,hideTitle,eventThumbnail,hideThumbnail";
		}
		private static EventItem decodeFromResultSet(ResultSet rs, int level) throws SQLException {
			EventItem ei = new EventItem();

			ei.setEventId(rs.getLong(1));
			if (rs.wasNull())
				ei.unsetEventId(); // should never happen

			ei.setEventCateg(rs.getInt(13));
			if (rs.wasNull())
				ei.unsetEventCateg();

			ei.setChildrenPools(new LinkedList<Long>());
			ei.setParentPool(rs.getLong(10));

			if (level < 10)
				return ei;

			ei.setEventTitle(rs.getString(6));
			ei.setEventThumbnail("http://pocketcampus.epfl.ch/images/padlock.png");
			ei.setSecondLine("Use the scan button to exchange contact information with this person");

			if (level < 100)
				return ei;

			Date startDate = rs.getTimestamp(2);
			if (startDate != null)
				ei.setStartDate(startDate.getTime());
			Date endDate = rs.getTimestamp(3);
			if (endDate != null)
				ei.setEndDate(endDate.getTime());
			ei.setFullDay(rs.getBoolean(4));
			if (rs.wasNull())
				ei.unsetFullDay();

			ei.setEventPicture(Utils.getResizedPhotoUrl(rs.getString(5), 500));
			ei.setEventThumbnail(Utils.getResizedPhotoUrl(rs.getString(21), 100));
			ei.setEventTitle(rs.getString(6));
			ei.setEventPlace(rs.getString(7));
			ei.setEventSpeaker(rs.getString(8));
			ei.setEventDetails(rs.getString(9));
			ei.setEventUri(rs.getString(11));
			ei.setVcalUid(rs.getString(12));

			ei.setEventTags(Utils.split(rs.getString(14), "[,]"));
			ei.setLocationHref(Utils.convertMapUrl(rs.getString(15)));
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
			ei.setEventPicture("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId="
					+ ei.getParentPool() + "%26markFavorite=" + ei.getEventId());
			// remove details
			ei.setEventDetails(null);
			// show help
			ei.setSecondLine("Allow others to scan your barcode to exchange contact information with them");
		}

		public static MyQuery getSelectPublicEventItemsQuery(String lang) {
			return new MyQuery().
					addPart("SELECT " + getSelectFields(lang) + ",userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN").
					addPart(" FROM eventitems LEFT JOIN eventusers ON eventId=mappedEvent").
					addPart(" WHERE (deleted IS NULL)").
					addPart(" AND (isProtected IS NULL)");
		}

		public static MyQuery getSelectAccessibleEventItemsQuery(List<String> token, String lang) {
			return new MyQuery().
					addPart("SELECT " + getSelectFields(lang) + ",permLevel AS PERM_LEVEL,userId AS USER_ID,exchangeToken AS EXCHANGE_TOKEN").
					addPart(" FROM eventitems INNER JOIN eventperms ON eventItemId=eventId LEFT JOIN eventusers ON eventId=mappedEvent").
					addPart(" WHERE (deleted IS NULL)").
					addPartWithList(" AND (userToken IN ?)", token, " AND (1=0)");
		}

		public static MyQuery getFillChildrenEventPoolsQuery() {
			return new MyQuery().
					addPart("SELECT eventId,poolId FROM eventitems,eventpools WHERE (eventId=parentEvent)");
		}

		public static Map<Long, EventItem> getEventItemsUsingQueries(Connection conn, MyQuery publicItemsQuery, MyQuery accessibleItemsQuery, MyQuery fillChildrenQuery,
				List<String> token) throws SQLException {
			PreparedStatement stm;
			ResultSet rs;
			Map<Long, EventItem> items = new HashMap<Long, EventItem>();

			stm = publicItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while (rs.next()) {
				EventItem ei = decodeFromResultSet(rs, 100);
				if (token.contains(rs.getString("USER_ID")))
					makeSelfEvent(ei, rs.getString("EXCHANGE_TOKEN"));
				items.put(ei.getEventId(), ei);
			}
			rs.close();
			stm.close();

			stm = accessibleItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while (rs.next()) {
				if (rs.getInt("PERM_LEVEL") == 0)
					continue;
				EventItem ei = decodeFromResultSet(rs, rs.getInt("PERM_LEVEL"));
				if (token.contains(rs.getString("USER_ID")))
					makeSelfEvent(ei, rs.getString("EXCHANGE_TOKEN"));
				items.put(ei.getEventId(), ei);
			}
			rs.close();
			stm.close();

			// Now fill children
			stm = fillChildrenQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while (rs.next()) {
				if (items.get(rs.getLong(1)) != null)
					items.get(rs.getLong(1)).getChildrenPools().add(rs.getLong(2));
			}
			rs.close();
			stm.close();

			return items;
		}
	}

	public static Map<Long, EventItem> eventItemsFromDb(Connection conn, long parentId, int periodInMinutes, List<String> token, String lang) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery(lang).
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token, lang).
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithValue(" AND (parentPool=?)", new Long(parentId));
		if (periodInMinutes > 0) {
			String timeConstraint = " AND (TIMESTAMPDIFF(MINUTE,NOW(),startDate)<? AND TIMESTAMPDIFF(MINUTE,NOW(),endDate)>=0)";
			publicEvents.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
			accessibleEvents.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
			fillChildren.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
		} else if (periodInMinutes < 0) {
			String timeConstraint = " AND (TIMESTAMPDIFF(MINUTE,NOW(),endDate)<0 AND TIMESTAMPDIFF(MINUTE,NOW(),endDate)>=?)";
			publicEvents.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
			accessibleEvents.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
			fillChildren.addPartWithValue(timeConstraint, new Integer(periodInMinutes));
		}
		return EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
	}

	public static EventItem eventItemFromDb(Connection conn, long id, List<String> token, String lang) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery(lang).
				addPartWithValue(" AND (eventId=?)", new Long(id));
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token, lang).
				addPartWithValue(" AND (eventId=?)", new Long(id));
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithValue(" AND (eventId=?)", new Long(id));
		Map<Long, EventItem> res = EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
		if (res.size() == 0)
			return null;
		return res.get(res.keySet().iterator().next());
	}

	public static Map<Long, EventItem> eventItemsByIds(Connection conn, List<Long> ids, List<String> token, String lang) throws SQLException {
		MyQuery publicEvents = EventItemDecoderFromDb.getSelectPublicEventItemsQuery(lang).
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		MyQuery accessibleEvents = EventItemDecoderFromDb.getSelectAccessibleEventItemsQuery(token, lang).
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		MyQuery fillChildren = EventItemDecoderFromDb.getFillChildrenEventPoolsQuery().
				addPartWithList(" AND (eventId IN ?)", ids, " AND (1=0)");
		return EventItemDecoderFromDb.getEventItemsUsingQueries(conn, publicEvents, accessibleEvents, fillChildren, token);
	}

}
