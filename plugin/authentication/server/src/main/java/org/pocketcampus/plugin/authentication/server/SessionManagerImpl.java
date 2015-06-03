package org.pocketcampus.plugin.authentication.server;

import ch.epfl.tequila.client.model.TequilaPrincipal;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * SessionManager to manage PC sessions.
 * 
 * @author Amer Chamseddine <amer.chamseddine@epfl.ch>
 */
public class SessionManagerImpl implements SessionManager {

	private final static long TIMEOUT_INTERVAL = 3600; // 1 hour
	private final static long EXPIRY_INTERVAL = 30 * 24 * 3600; // 1 month

	private final ConnectionManager mConnectionManager;

	public SessionManagerImpl() {
		this.mConnectionManager = new ConnectionManager(PocketCampusServer.CONFIG.getString("DB_URL"),
				PocketCampusServer.CONFIG.getString("DB_USERNAME"), PocketCampusServer.CONFIG.getString("DB_PASSWORD"));
		new Thread(getCleaner()).start();
	}

	private long getNow() {
		return System.currentTimeMillis() / 1000;
	}

	@Override
	public String insert(TequilaPrincipal principal, boolean rememberMe) {
		String id = UUID.randomUUID().toString();
		PreparedStatement sqlStm = null;
		long now = getNow();
		try {
			sqlStm = mConnectionManager
					.getConnection()
					.prepareStatement(
							"REPLACE INTO `authsessions` (`sessionid`, `expiry`, `timeout`, `clienthost`, `office`, `phone`, `status`, `firstname`, `where`, `requesthost`, `version`, `unit`, `sciper`, `title`, `gaspar`, `email`, `category`, `lastname`, `authorig`, `unixid`, `groupid`, `authstrength`) VALUES ("
									// Slight hack to repeat a string, it avoids a dependency on Commons Lang...
									+ new String(new char[22]).replace("\0", ", ?").substring(2) + ")");
			sqlStm.setString(1, id);
			sqlStm.setLong(2, now + EXPIRY_INTERVAL); // 0 = never expires
			sqlStm.setLong(3, rememberMe ? 0 : now + TIMEOUT_INTERVAL); // 0 = never times out
			sqlStm.setString(4, principal.getHost());
			sqlStm.setString(5, principal.getAttribute("office"));
			sqlStm.setString(6, principal.getAttribute("phone"));
			sqlStm.setString(7, principal.getAttribute("status"));
			sqlStm.setString(8, principal.getAttribute("firstname"));
			sqlStm.setString(9, principal.getAttribute("where"));
			sqlStm.setString(10, principal.getAttribute("requesthost"));
			sqlStm.setString(11, principal.getAttribute("version"));
			sqlStm.setString(12, principal.getAttribute("unit"));
			sqlStm.setString(13, principal.getAttribute("uniqueid"));
			sqlStm.setString(14, principal.getAttribute("title"));
			sqlStm.setString(15, principal.getAttribute("username"));
			sqlStm.setString(16, principal.getAttribute("email"));
			sqlStm.setString(17, principal.getAttribute("categorie"));
			sqlStm.setString(18, principal.getAttribute("name"));
			sqlStm.setString(19, principal.getAttribute("authorig"));
			sqlStm.setString(20, principal.getAttribute("unixid"));
			sqlStm.setString(21, principal.getAttribute("groupid"));
			sqlStm.setString(22, principal.getAttribute("authstrength"));
			sqlStm.executeUpdate();
			return id;
		} catch (SQLException e) {
			System.out.println("[auth] Problem in insert");
			return null;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<String> getFields(String sessionId, List<String> fields) {
		touch(sessionId);
		PreparedStatement sqlStm = null;
		try {
			StringBuilder fieldsBuilder = new StringBuilder();
			for (int n = 0; n < fields.size() - 1; n++) {
				fieldsBuilder.append(fields.get(n));
				fieldsBuilder.append(", ");
			}
			fieldsBuilder.append(fields.get(fields.size() - 1));

			sqlStm = mConnectionManager.getConnection().prepareStatement("SELECT " + fieldsBuilder.toString() + " FROM `authsessions` WHERE `sessionid` = ?");
			sqlStm.setString(1, sessionId);
			ResultSet rs = sqlStm.executeQuery();
			if (rs.next()) {
				List<String> res = new LinkedList<>();
				for (int i = 1; i <= fields.size(); i++)
					res.add(rs.getString(i));
				return res;
			}
			return null;
		} catch (SQLException e) {
			System.out.println("[auth] Problem with select user");
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Integer destroySessions(String sciper) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM `authsessions` WHERE `sciper` = ?");
			sqlStm.setString(1, sciper);
			return sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("[auth] Problem while destroying sessions");
			e.printStackTrace();
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Runnable getCleaner() {
		return new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(60 * 1000); // 1 minute
						cleanup();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void touch(String sessionId) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE `authsessions` SET `timeout` = ? WHERE `sessionid` = ? AND `timeout` <> 0");
			sqlStm.setLong(1, getNow() + TIMEOUT_INTERVAL);
			sqlStm.setString(2, sessionId);
			sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("[auth] Problem with updating timeout");
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void cleanup() {
		PreparedStatement sqlStm = null;
		long now = getNow();
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM `authsessions` WHERE (`expiry` <> 0 AND `expiry` < ?) OR (`timeout` <> 0 AND `timeout` < ?)");
			sqlStm.setLong(1, now);
			sqlStm.setLong(2, now);
			sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("[auth] Problem while cleaning up");
			e.printStackTrace();
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
