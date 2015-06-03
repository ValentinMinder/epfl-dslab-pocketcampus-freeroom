package org.pocketcampus.plugin.events.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBUtils {

	public static Map<String, String> getTagsFromDb(Connection conn, boolean mementoOnly, String lang) throws SQLException {
		String feedValue = "CASE WHEN feedValue is not null THEN feedValue ELSE feedValue_fr END AS feedValue"; 
		if("fr".equalsIgnoreCase(lang)) {
			feedValue = "CASE WHEN feedValue_fr is not null THEN feedValue_fr ELSE feedValue END AS feedValue_fr"; 
		}
		Map<String, String> feeds = new HashMap<String, String>();
		PreparedStatement stm = conn.prepareStatement("SELECT feedKey," + feedValue + " FROM eventtags " + (mementoOnly ? "WHERE isMemento=1" : ""));
		ResultSet rs = stm.executeQuery();
		while (rs.next()) {
			feeds.put(rs.getString(1), rs.getString(2));
		}
		rs.close();
		stm.close();
		return feeds;
	}

	public static Map<Integer, String> getCategsFromDb(Connection conn, String lang) throws SQLException {
		String categValue = "CASE WHEN categValue is not null THEN categValue ELSE categValue_fr END AS categValue"; 
		if("fr".equalsIgnoreCase(lang)) {
			categValue = "CASE WHEN categValue_fr is not null THEN categValue_fr ELSE categValue END AS categValue_fr"; 
		}
		Map<Integer, String> categs = new HashMap<Integer, String>();
		PreparedStatement stm = conn.prepareStatement("SELECT categKey," + categValue + " FROM eventcategs;");
		ResultSet rs = stm.executeQuery();
		while (rs.next()) {
			categs.put(rs.getInt(1), rs.getString(2));
		}
		rs.close();
		stm.close();
		return categs;
	}

	

	public static boolean isSubeventOf(Connection conn, long event, long parent, Set<Long> history) throws SQLException {
		if (event == parent)
			return true;
		event = getGrandParentEventItemId(conn, event);
		if (history.contains(event))
			return false; // get rid of loops
		history.add(event);
		if (history.size() > 20)
			return false; // limit recursion depth (screw it if it is more than 20)
		if (event == 0)
			return false;
		return isSubeventOf(conn, event, parent, history);
	}

	public static long getGrandParentEventItemId(Connection conn, long id) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;

		long grandParent = 0;
		stm = conn.prepareStatement("SELECT parentEvent FROM eventitems INNER JOIN eventpools ON parentPool = poolId WHERE eventId = ?;");
		stm.setLong(1, id);
		rs = stm.executeQuery();
		if (rs.next()) {
			grandParent = rs.getLong(1);
		}
		rs.close();
		stm.close();

		return grandParent;
	}

	

	public static List<Long> filterStarred(Connection conn, List<Long> starred, long parentEvent) throws SQLException {
		List<Long> filtered = new LinkedList<Long>();
		for (Long l : starred) {
			if (isSubeventOf(conn, l, parentEvent, new HashSet<Long>()))
				filtered.add(l);
		}
		return filtered;
	}

	public static String userTokenFromExchangeToken(Connection conn, String exchangeToken) throws SQLException {
		PreparedStatement stm;
		ResultSet rs;

		String userToken = null;
		stm = conn.prepareStatement("SELECT userId FROM eventusers WHERE exchangeToken=?;");
		stm.setString(1, exchangeToken);
		rs = stm.executeQuery();
		if (rs.next()) {
			userToken = rs.getString(1);
		}
		rs.close();
		stm.close();

		return userToken;
	}

	public static int exchangeContacts(Connection conn, String userToken1, String userToken2) throws SQLException {
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

	public static void logPageView(Connection conn, List<String> userTickets, long nodeId, String pageType) throws SQLException {
		for (String ticket : userTickets) {
			PreparedStatement stm = conn
					.prepareStatement("INSERT INTO eventpageviews (userTicket, nodeId, pageType, viewCount) VALUES (?, ?, ?, 1) ON DUPLICATE KEY UPDATE viewCount = viewCount + 1;");
			stm.setString(1, ticket);
			stm.setLong(2, nodeId);
			stm.setString(3, pageType);
			stm.executeUpdate();
			stm.close();
		}
	}

	
}
