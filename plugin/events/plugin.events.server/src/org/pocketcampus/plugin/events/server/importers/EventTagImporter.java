package org.pocketcampus.plugin.events.server.importers;

import com.google.gson.Gson;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.events.server.utils.MyQuery;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventTagImporter {

	public static class MementoTag {
		
		public String en_label; // : "SV"
		public String en_name; // : "Life Sciences"
		public String entity; // : "SV (Faculté des sciences de la vie)"
		public String fr_label; // : "SV"
		public String fr_name; // : "Sciences de la Vie"
		public boolean is_main_memento; // : true
		public long memento_id; // : 9
		public String slug; // : "sv"

	}
	
	public static void importTagsFromMemento(Connection conn) {
		String json = null;
		try {
			json = new HttpClientImpl().get("http://memento.epfl.ch/feeds/mementos/", Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(json == null) {
			System.out.println("failed to get tags from Memento... aborting");
			return;
		}
		Gson gson = new Gson();
		MementoTag [] tags = gson.fromJson(json, MementoTag[].class);
		System.out.println("received " + tags.length + " tags from Memento");
		for(MementoTag t : tags) {
			try {
				insertUpdateEventTag(t, conn);
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("failed to insert/update tag " + t.slug + "... skipping");
			}
		}
		System.out.println("finished importing tags");
	}
	
	private static void insertUpdateEventTag(MementoTag t, Connection conn) throws SQLException {
		// empty string means not set
		if("".equals(t.en_label)) t.en_label = null;
		if("".equals(t.fr_label)) t.fr_label = null;
		MyQuery q = new MyQuery();
		q.addPart("REPLACE INTO eventtags SET ");
		q.addPartWithValue("`feedKey` = ?,", t.slug);
		q.addPartWithValue("`feedValue` = ?,", t.en_label != null ? t.en_label : t.en_name);
		q.addPartWithValue("`feedValue_fr` = ?,", t.fr_label != null ? t.fr_label : t.fr_name);
		q.addPartWithValue("`isMemento` = ?", 1);
		PreparedStatement stm = q.getPreparedStatement(conn);
		stm.executeUpdate();
		stm.close();
		System.out.println("inserted/updated tag " + t.slug);
	}
	
}
