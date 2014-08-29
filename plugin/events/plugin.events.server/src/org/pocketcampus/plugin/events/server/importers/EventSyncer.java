package org.pocketcampus.plugin.events.server.importers;

import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EventSyncer {

	public static void syncWithMemento(Connection conn) {
		List<Long> ids = null;
		try {
			ids = getIdsOfMementoEventsFromDb(conn);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if(ids == null) {
			System.out.println("couldn't get non-deleted events from db... aborting");
			return;
		}
		System.out.println(ids.size() + " non-deleted memento events in the db");
		int deleted = 0;
		for(long i : ids) {
			boolean toDelete = false;
			try {
				new URL("http://memento.epfl.ch/event/export/" + i).openConnection().getInputStream();
			} catch (Exception e) {
				if(e instanceof FileNotFoundException) {
					toDelete = true;
				} else {
					e.printStackTrace();
				}
			}
			try {
				if(toDelete) {
					markMementoEventDeleted(i, conn);
					deleted++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("couldn't mark deleted event " + i + "... skipping");
			}
		}
		System.out.println(deleted + " memento events were marked as deleted");
	}

	private static List<Long> getIdsOfMementoEventsFromDb(Connection conn) throws SQLException {
		List<Long> ids = new LinkedList<Long>();
		PreparedStatement stm = conn.prepareStatement("SELECT eventId FROM eventitems WHERE eventUri IS NOT NULL AND deleted IS NULL;");
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
	
	
}
