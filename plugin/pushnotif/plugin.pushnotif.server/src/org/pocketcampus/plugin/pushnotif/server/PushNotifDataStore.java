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
import org.pocketcampus.plugin.pushnotif.shared.PlatformType;

import ch.epfl.tequila.client.model.TequilaPrincipal;

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

	public boolean insertUser(TequilaPrincipal principal) {
		if (principal == null)
			return false;
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("REPLACE INTO users (gaspar, org, host, sciper, email, first, last, path, unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			sqlStm.setString(1, principal.getUser());
			sqlStm.setString(2, principal.getOrg());
			sqlStm.setString(3, principal.getHost());
			sqlStm.setString(4, principal.getAttribute("uniqueid"));
			sqlStm.setString(5, principal.getAttribute("email"));
			sqlStm.setString(6, principal.getAttribute("firstname"));
			sqlStm.setString(7, principal.getAttribute("name"));
			sqlStm.setString(8, principal.getAttribute("where"));
			sqlStm.setString(9, principal.getAttribute("unit"));
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[pushnotif] Problem in insert user");
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
	
	public boolean insertPushToken(String gaspar, PlatformType platform, String pushkey) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("REPLACE INTO pushnotif (gaspar, platform, pushkey) VALUES (?, ?, ?)");
			sqlStm.setString(1, gaspar);
			sqlStm.setString(2, translatePlatformType(platform));
			sqlStm.setString(3, pushkey);
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
	
	public boolean updatePushToken(PlatformType platform, String oldPushkey, String newPushkey) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE pushnotif SET pushkey = ? WHERE platform = ? AND pushkey = ?");
			sqlStm.setString(1, newPushkey);
			sqlStm.setString(2, translatePlatformType(platform));
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
	
	public boolean deletePushToken(PlatformType platform, String pushkey) {
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM pushnotif WHERE platform = ? AND pushkey = ?");
			sqlStm.setString(1, translatePlatformType(platform));
			sqlStm.setString(2, pushkey);
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
	
	public List<String> selectTokens(List<String> gaspars, PlatformType platform) {
		PreparedStatement sqlStm = null;
		try {
			String markers = StringUtils.repeat(", ?", gaspars.size()).substring(2);
			sqlStm = mConnectionManager.getConnection().prepareStatement("SELECT pushkey FROM pushnotif WHERE platform = ? AND gaspar IN (" + markers + ")");
			sqlStm.setString(1, translatePlatformType(platform));
			LinkedList<String> tokens = new LinkedList<String>();
			for(int i = 0; i < gaspars.size(); i++) {
				sqlStm.setString(i + 2, gaspars.get(i));
			}
			ResultSet rs = sqlStm.executeQuery();
			while(rs.next()) {
				tokens.add(rs.getString("pushkey"));
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
	
	private String translatePlatformType(PlatformType platform) {
		switch(platform) {
		case PC_PLATFORM_ANDROID:
			return "ANDROID";
		case PC_PLATFORM_IOS:
			return "IOS";
		default:
			return "X";
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
