package org.pocketcampus.plugin.events.server.importers;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				Connection conn = null;
				try {
					conn = connMgr.getConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(conn == null) {
					System.out.println("couldn't connect to db... aborting");
					return;
				}
				EventCategImporter.importCategsFromMemento(conn);
				EventTagImporter.importTagsFromMemento(conn);
				EventItemImporter.importEventsFromMemento(conn);
				EventItemSyncer.syncWithMemento(conn);
				System.out.println("Finished Async Import on " + dateLastImport);
			}
		}).start();
	}

}
