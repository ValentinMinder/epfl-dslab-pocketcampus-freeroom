package org.pocketcampus.plugin.pushnotif.server;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushNotifDataStore {
	private ConnectionManager mConnectionManager;

	public PushNotifDataStore() {
		this.mConnectionManager = new ConnectionManager(PocketCampusServer.CONFIG.getString("DB_URL"),
				PocketCampusServer.CONFIG.getString("DB_USERNAME"), PocketCampusServer.CONFIG.getString("DB_PASSWORD"));
	}

	public boolean insertMapping(String plugin, String userid, String platform, String pushtoken) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("REPLACE INTO pc_pushnotif (plugin, userid, platform, pushtoken) VALUES (?, ?, ?, ?)");
			sqlStm.setString(1, plugin);
			sqlStm.setString(2, userid);
			sqlStm.setString(3, platform);
			sqlStm.setString(4, pushtoken);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[pushnotif] Problem in insert token");
			return false;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean updatePushToken(String platform, String oldPushkey, String newPushkey) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE pc_pushnotif SET pushtoken = ? WHERE platform = ? AND pushtoken = ?");
			sqlStm.setString(1, newPushkey);
			sqlStm.setString(2, platform);
			sqlStm.setString(3, oldPushkey);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[pushnotif] Problem in updating token");
			return false;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean deletePushToken(String platform, String pushtoken) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM pc_pushnotif WHERE platform = ? AND pushtoken = ?");
			sqlStm.setString(1, platform);
			sqlStm.setString(2, pushtoken);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[pushnotif] Problem in deleting token");
			return false;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, String> selectTokens(String plugin, List<String> userIds, String platform) {
		// TODO WARNING query size is proportional to userList size, we might hit a limit on the query size!!
		PreparedStatement sqlStm = null;
		try {
			String markers = new String(new char[userIds.size()]).replace("\0", ", ?").substring(2);
			sqlStm = mConnectionManager.getConnection().prepareStatement(
					"SELECT pushtoken, userid FROM pc_pushnotif WHERE plugin = ? AND platform = ? AND userid IN (" + markers + ")");
			sqlStm.setString(1, plugin);
			sqlStm.setString(2, platform);
			Map<String, String> tokens = new HashMap<String, String>();
			for (int i = 0; i < userIds.size(); i++) {
				sqlStm.setString(i + 3, userIds.get(i));
			}
			ResultSet rs = sqlStm.executeQuery();
			while (rs.next()) {
				tokens.put(rs.getString("pushtoken"), rs.getString("userid"));
			}
			return tokens;
		} catch (SQLException e) {
			System.out.println("[pushnotif] Problem in select tokens");
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

	/*
	 * public void updateCookie(String colName, String cookieVal, String sciper) {
	 * PreparedStatement sqlStm = null;
	 * try {
	 * sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE auth_tokens SET " + colName + " = ? WHERE sciper = ?");
	 * sqlStm.setString(1, cookieVal);
	 * sqlStm.setString(2, sciper);
	 * sqlStm.executeUpdate();
	 * } catch (SQLException e) {
	 * System.out.println("<Auth> Problem in insert user.");
	 * } finally {
	 * try {
	 * if (sqlStm != null)
	 * sqlStm.close();
	 * } catch (SQLException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 * 
	 * public int killCookie(String colName, String cookieVal) {
	 * PreparedStatement sqlStm = null;
	 * try {
	 * sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE auth_tokens SET " + colName + " = NULL WHERE " + colName + " = ?");
	 * sqlStm.setString(1, cookieVal);
	 * sqlStm.executeUpdate();
	 * return 200;
	 * } catch (SQLException e) {
	 * System.out.println("<Auth> Problem in delete user.");
	 * return 500;
	 * } finally {
	 * try {
	 * if (sqlStm != null)
	 * sqlStm.close();
	 * } catch (SQLException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 * 
	 * public void insertTequilaCookie(String tequilaCookie) {
	 * PreparedStatement sqlStm = null;
	 * try {
	 * sqlStm = mConnectionManager.getConnection().prepareStatement("INSERT IGNORE INTO cookies (tequila) VALUES (?)");
	 * sqlStm.setString(1, tequilaCookie);
	 * sqlStm.executeUpdate();
	 * } catch (SQLException e) {
	 * System.out.println("<Auth> Problem in insertTequilaCookie");
	 * } finally {
	 * try {
	 * if (sqlStm != null)
	 * sqlStm.close();
	 * } catch (SQLException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 * 
	 * public void deleteTequilaCookie(String tequilaCookie) {
	 * PreparedStatement sqlStm = null;
	 * try {
	 * sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM cookies WHERE tequila = ?");
	 * sqlStm.setString(1, tequilaCookie);
	 * sqlStm.executeUpdate();
	 * } catch (SQLException e) {
	 * System.out.println("<Auth> Problem in deleteTequilaCookie");
	 * } finally {
	 * try {
	 * if (sqlStm != null)
	 * sqlStm.close();
	 * } catch (SQLException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 */

}
