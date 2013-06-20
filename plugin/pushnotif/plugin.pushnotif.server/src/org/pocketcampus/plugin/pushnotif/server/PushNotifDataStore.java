package org.pocketcampus.plugin.pushnotif.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;

public class PushNotifDataStore {
	private ConnectionManager mConnectionManager;
	
	public PushNotifDataStore() {
		try {
			this.mConnectionManager = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			e.printStackTrace();
		}

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
	
	public List<String> selectTokens(String plugin, List<String> userIds, String platform) {
		PreparedStatement sqlStm = null;
		try {
			String markers = StringUtils.repeat(", ?", userIds.size()).substring(2);
			sqlStm = mConnectionManager.getConnection().prepareStatement("SELECT pushtoken FROM pc_pushnotif WHERE plugin = ? AND platform = ? AND userid IN (" + markers + ")");
			sqlStm.setString(1, plugin);
			sqlStm.setString(2, platform);
			LinkedList<String> tokens = new LinkedList<String>();
			for(int i = 0; i < userIds.size(); i++) {
				sqlStm.setString(i + 3, userIds.get(i));
			}
			ResultSet rs = sqlStm.executeQuery();
			while(rs.next()) {
				tokens.add(rs.getString("pushtoken"));
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
	

	
	/*public void updateCookie(String colName, String cookieVal, String sciper) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE auth_tokens SET " + colName + " = ? WHERE sciper = ?");
			sqlStm.setString(1, cookieVal);
			sqlStm.setString(2, sciper);
			sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<Auth> Problem in insert user.");
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int killCookie(String colName, String cookieVal) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE auth_tokens SET " + colName + " = NULL WHERE " + colName + " = ?");
			sqlStm.setString(1, cookieVal);
			sqlStm.executeUpdate();
			return 200;
		} catch (SQLException e) {
			System.out.println("<Auth> Problem in delete user.");
			return 500;
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertTequilaCookie(String tequilaCookie) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("INSERT IGNORE INTO cookies (tequila) VALUES (?)");
			sqlStm.setString(1, tequilaCookie);
			sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<Auth> Problem in insertTequilaCookie");
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteTequilaCookie(String tequilaCookie) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM cookies WHERE tequila = ?");
			sqlStm.setString(1, tequilaCookie);
			sqlStm.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<Auth> Problem in deleteTequilaCookie");
		} finally {
			try {
				if (sqlStm != null)
					sqlStm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}*/
	
}
