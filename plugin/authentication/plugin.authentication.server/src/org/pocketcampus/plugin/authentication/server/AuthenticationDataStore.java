package org.pocketcampus.plugin.authentication.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;

public class AuthenticationDataStore {
	private ConnectionManager mConnectionManager;
	
	public AuthenticationDataStore() {
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
			sqlStm = mConnectionManager.getConnection().prepareStatement("REPLACE INTO pc_authentication (plugin, userid, platform, pushtoken) VALUES (?, ?, ?, ?)");
			sqlStm.setString(1, plugin);
			sqlStm.setString(2, userid);
			sqlStm.setString(3, platform);
			sqlStm.setString(4, pushtoken);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[authentication] Problem in insert token");
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
			sqlStm = mConnectionManager.getConnection().prepareStatement("UPDATE pc_authentication SET pushtoken = ? WHERE platform = ? AND pushtoken = ?");
			sqlStm.setString(1, newPushkey);
			sqlStm.setString(2, platform);
			sqlStm.setString(3, oldPushkey);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[authentication] Problem in updating token");
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
			sqlStm = mConnectionManager.getConnection().prepareStatement("DELETE FROM pc_authentication WHERE platform = ? AND pushtoken = ?");
			sqlStm.setString(1, platform);
			sqlStm.setString(2, pushtoken);
			sqlStm.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("[authentication] Problem in updating token");
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
			String markers = StringUtils.repeat(", ?", userIds.size()).substring(2);
			sqlStm = mConnectionManager.getConnection().prepareStatement("SELECT pushtoken, userid FROM pc_authentication WHERE plugin = ? AND platform = ? AND userid IN (" + markers + ")");
			sqlStm.setString(1, plugin);
			sqlStm.setString(2, platform);
			Map<String, String> tokens = new HashMap<String, String>();
			for(int i = 0; i < userIds.size(); i++) {
				sqlStm.setString(i + 3, userIds.get(i));
			}
			ResultSet rs = sqlStm.executeQuery();
			while(rs.next()) {
				tokens.put(rs.getString("pushtoken"), rs.getString("userid"));
			}
			return tokens;
		} catch (SQLException e) {
			System.out.println("[authentication] Problem in select tokens");
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
	

	
}
