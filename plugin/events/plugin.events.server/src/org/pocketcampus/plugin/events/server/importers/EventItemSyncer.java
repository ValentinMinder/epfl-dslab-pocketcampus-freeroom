package org.pocketcampus.plugin.events.server.importers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EventItemSyncer {

	private static final String MEMENTO_EVENT_URL = "http://memento.epfl.ch/feeds/event/?id=";
	
	public static void syncWithMemento(Connection conn) {
		Map<Long, Translations> idMap = null;
		try {
			idMap = getIdsOfMementoEventsFromDb(conn);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if(idMap == null) {
			System.out.println("Events: couldn't get non-deleted events from db... aborting");
			return;
		}
		System.out.println("Events: " + idMap.size() + " non-deleted memento events in the db");
		int deleted = 0;
		for(Entry<Long, Translations> e : idMap.entrySet()) {
			if(e.getValue().enTr == null && e.getValue().frTr == null) {
				// make sure we don't delete non-memento events
				// should never happen because they are excluded in select query
				System.out.println("Events: ERROR is this even a Memento event? skipping");
				continue;
			}
			boolean oneTranslationAvailable = false;
			if(e.getValue().enTr != null) {
				try {
					new URL(MEMENTO_EVENT_URL + e.getValue().enTr).openConnection().getInputStream();
					oneTranslationAvailable = true;
				} catch (FileNotFoundException ex) {
					// silent because this is the behavior when event (translation object) is deleted
				} catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Events: ERROR IOException dunno what to do... skipping");
					continue;
				}
			}
			if(e.getValue().frTr != null) {
				try {
					new URL(MEMENTO_EVENT_URL + e.getValue().frTr).openConnection().getInputStream();
					oneTranslationAvailable = true;
				} catch (FileNotFoundException ex) {
					// silent because this is the behavior when event (translation object) is deleted
				} catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("Events: ERROR IOException dunno what to do... skipping");
					continue;
				}
			}
			try {
				if(!oneTranslationAvailable) {
					markMementoEventDeleted(e.getKey(), conn);
					deleted++;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				System.out.println("Events: couldn't mark deleted event " + e.getKey() + "... skipping");
			}
		}
		System.out.println("Events: " + deleted + " memento events were marked as deleted");
	}
	
	private static class Translations {
		public Long frTr = null;
		public Long enTr = null;
	}

	private static Map<Long, Translations> getIdsOfMementoEventsFromDb(Connection conn) throws SQLException {
		Map<Long, Translations> idMap = new HashMap<>();
		PreparedStatement stm = conn.prepareStatement("SELECT eventId,translation,translation_fr FROM eventitems WHERE (translation IS NOT NULL OR translation_fr IS NOT NULL) AND deleted IS NULL;");
		ResultSet rs = stm.executeQuery();
		while (rs.next()) {
			Translations tr = new Translations();
			idMap.put(rs.getLong(1), tr);
			long enTr = rs.getLong(2);
			if(!rs.wasNull())
				tr.enTr = enTr;
			long frTr = rs.getLong(3);
			if(!rs.wasNull())
				tr.frTr = frTr;
		}
		rs.close();
		stm.close();
		return idMap;
	}
	
	private static void markMementoEventDeleted(long id, Connection conn) throws SQLException {
		PreparedStatement stm = conn.prepareStatement("UPDATE eventitems SET deleted = 1 WHERE eventId = ?;");
		stm.setLong(1, id);
		stm.executeUpdate();
		stm.close();
	}
	
	
}
