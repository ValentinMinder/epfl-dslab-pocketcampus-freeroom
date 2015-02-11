package org.pocketcampus.plugin.events.server.importers;

import com.google.gson.Gson;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.events.server.utils.MyQuery;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventCategImporter {

	public static class MementoCateg {
		public String code; // : "CONF", 
		public long category_id; // : 1
		public String en_label; // : "Conferences - Seminars", 
		public String fr_label; // : "Conf\u00e9rences - S\u00e9minaires"
	}
	
	public static void importCategsFromMemento(Connection conn) {
		String json = null;
		try {
			json = new HttpClientImpl().get("http://memento.epfl.ch/feeds/categories/", Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(json == null) {
			System.out.println("Events: failed to get categs from Memento... aborting");
			return;
		}
		Gson gson = new Gson();
		MementoCateg [] categs = gson.fromJson(json, MementoCateg[].class);
		System.out.println("Events: received " + categs.length + " categs from Memento");
		for(MementoCateg c : categs) {
			try {
				insertUpdateEventCateg(c, conn);
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("Events: failed to insert/update categ " + c.code + "... skipping");
			}
		}
		System.out.println("Events: finished importing categs");
	}
	
	private static void insertUpdateEventCateg(MementoCateg e, Connection conn) throws SQLException {
		MyQuery q = new MyQuery();
		q.addPart("REPLACE INTO eventcategs SET ");
		q.addPartWithValue("`categKey` = ?,", e.category_id);
		q.addPartWithValue("`categValue` = ?,", e.en_label);
		q.addPartWithValue("`categValue_fr` = ?", e.fr_label);
		PreparedStatement stm = q.getPreparedStatement(conn);
		stm.executeUpdate();
		stm.close();
		System.out.println("Events: inserted/updated categ " + e.code);
	}
	
}
