package org.pocketcampus.plugin.authentication.server;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import ch.epfl.tequila.client.model.TequilaPrincipal;

public class AuthDB {
	private ConnectionManager mConnectionManager;
	
	public AuthDB() {
		try {
			this.mConnectionManager = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	public String insertUser(TequilaPrincipal principal) {
		if (principal == null)
			return null;
		PreparedStatement sqlStm = null;
		try {
			sqlStm = mConnectionManager.getConnection().prepareStatement("INSERT IGNORE INTO auth_tokens (sciper, user, org, first, last, email, pc_cookie, moodle_cookie, camipro_cookie, isa_cookie) VALUES (?, ?, ?, ?, ?, ?, NULL, NULL, NULL, NULL)");
			sqlStm.setString(1, principal.getAttribute("uniqueid"));
			sqlStm.setString(2, principal.getUser());
			sqlStm.setString(3, principal.getOrg());
			sqlStm.setString(4, principal.getAttribute("firstname"));
			sqlStm.setString(5, principal.getAttribute("name"));
			sqlStm.setString(6, principal.getAttribute("email"));
			sqlStm.executeUpdate();
			return principal.getAttribute("uniqueid");
		} catch (SQLException e) {
			System.out.println("<Auth> Problem in insert user.");
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
	
	public void updateCookie(String colName, String cookieVal, String sciper) {
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
	}
	
}
