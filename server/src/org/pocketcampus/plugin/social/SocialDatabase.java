package org.pocketcampus.plugin.social;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

import org.pocketcampus.core.database.ConnectionManager;
import org.pocketcampus.core.database.handlers.exceptions.SQLIntegrityConstraintViolationExceptionHandler;
import org.pocketcampus.core.database.handlers.requests.CountRequestHandler;
import org.pocketcampus.core.database.handlers.requests.QueryRequestHandler;
import org.pocketcampus.core.database.handlers.requests.UpdateRequestHandler;
import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.shared.plugin.map.Position;
import org.pocketcampus.shared.plugin.social.User;

public class SocialDatabase {
	private final static String dbHost = "jdbc:mysql://localhost:3306/pocketcampus";
	private final static String dbUser = "root";
	private final static String dbPass = "pocketcampus";
	
	private static final String contactTable = "social_contact";
	private static final String pendingTable = "social_pending";
	private static final String permissionTable = "social_permissions";
	private static final String positionTable = "social_positions";

	/**
	 * @param a
	 * @param b
	 * @return true if a and b are friends already
	 */
	public static boolean testFriend(final User a, final User b) throws ServerException {
		String sqlRequest = "SELECT COUNT(*) AS `count` FROM `"+contactTable+"` WHERE `from` = ? AND `to` = ?";
		
		CountRequestHandler rf = new CountRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass), "count") {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, a.getIdFormat());
				stmt.setString(2, b.getIdFormat());
			}
		};

		int count = rf.execute();

		return (count == 1) ? true : false;
	}

	public static boolean addFriend(final User a, final User b) throws ServerException {
		String sqlRequest = "INSERT INTO `"+contactTable+"` (`from`, `to`) VALUES (?, ?), (?, ?)";
		int numAffectedRows = 0;

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0), new ConnectionManager(dbHost, dbUser, dbPass)) {
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, a.getIdFormat());
				stmt.setString(2, b.getIdFormat());
				
				stmt.setString(3, b.getIdFormat());
				stmt.setString(4, a.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();
		
		return (numAffectedRows == 2) ? true : false;
	}

	public static boolean removeFriend(final User a, final User b) throws ServerException {
		String sqlRequest = "DELETE FROM `"+contactTable+"` WHERE `from` = ? AND `to` = ? LIMIT 1";

		int numAffectedRows = 0;

		UpdateRequestHandler rf1 = new UpdateRequestHandler(sqlRequest,new ConnectionManager(dbHost, dbUser, dbPass)) {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, a.getIdFormat());
				stmt.setString(2, b.getIdFormat());
			}
		};
		numAffectedRows += rf1.execute();

		UpdateRequestHandler rf2 = new UpdateRequestHandler(sqlRequest) {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, b.getIdFormat());
				stmt.setString(2, a.getIdFormat());
			}
		};
		numAffectedRows += rf2.execute();

		return (numAffectedRows == 2) ? true : false;
	}

	public static Collection<User> getFriends(final User user) throws ServerException {
		String sqlRequest = "SELECT `to` FROM `"+contactTable+"` WHERE `from` = ?";

		QueryRequestHandler<Collection<User>> rf = new QueryRequestHandler<Collection<User>>(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {
			
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, user.getIdFormat());
			}
			
			@Override
			public Collection<User> processResult(ResultSet result)
			throws SQLException {

				HashSet<User> friends = new HashSet<User>();
				result.beforeFirst();

				while (result.next()) {
					User user = new User(result.getString("to"));
					friends.add(user);
				}

				return friends;
			}
		};

		Collection<User> friends = rf.execute();
		return friends;
	}

	/**
	 * @param from
	 * @param to
	 * @return true if there's a request pending from from to to.
	 */
	public static boolean testPending(final User from, final User to) throws ServerException {
		String sqlRequest = "SELECT COUNT(*) AS `count` FROM `"+pendingTable+"` WHERE `from` = ? AND `to` = ?";

		CountRequestHandler rf = new CountRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass), "count") {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, from.getIdFormat());
				stmt.setString(2, to.getIdFormat());
			}
		};

		int count = rf.execute();

		return (count == 1) ? true : false;
	}

	public static boolean addPending(final User from, final User to) throws ServerException {
		String sqlRequest = "INSERT INTO `"+pendingTable+"` (`from`, `to`)" + " VALUES (?, ?)";
		int numAffectedRows = 0;

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0), new ConnectionManager(dbHost, dbUser, dbPass)) {
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, from.getIdFormat());
				stmt.setString(2, to.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();

		return (numAffectedRows == 1) ? true : false;
	}

	public static boolean removePending(final User from, final User to) throws ServerException {
		String sqlRequest = "DELETE FROM `"+pendingTable+"` WHERE `from` = ? AND `to` = ? LIMIT 1";

		int numAffectedRows = 0;

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, from.getIdFormat());
				stmt.setString(2, to.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();

		return (numAffectedRows == 1) ? true : false;
	}

	public static Collection<User> getPending(final User user) throws ServerException {
		String sqlRequest = "SELECT `from` FROM `"+pendingTable+"` WHERE `to` = ?";

		QueryRequestHandler<Collection<User>> rf = new QueryRequestHandler<Collection<User>>(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {
			
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, user.getIdFormat());
			}
			
			@Override
			public Collection<User> processResult(ResultSet result)
			throws SQLException {

				HashSet<User> pendings = new HashSet<User>();
				result.beforeFirst();

				while (result.next()) {
					User user = new User(result.getString("from"));
					pendings.add(user);
				}

				return pendings;
			}
		};

		Collection<User> pendings = rf.execute();
		return pendings;
	}

	public static boolean testPermission(final String service, final User user, final User target) throws ServerException {
		String sqlRequest = "SELECT COUNT(*) AS `count` FROM `"+permissionTable+"` WHERE `service_id` = ? AND `user` = ? AND `granted_to` = ?";

		CountRequestHandler rf = new CountRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass), "count") {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, service);
				stmt.setString(2, user.getIdFormat());
				stmt.setString(3, target.getIdFormat());
			}
		};

		int count = rf.execute();

		return (count == 1) ? true : false;
	}

	public static boolean addPermission(final String service, final User user, final User target) throws ServerException {
		String sqlRequest = "INSERT INTO `"+permissionTable+"` (`service_id`, `user`, `granted_to`)" + " VALUES (?, ?, ?)";
		int numAffectedRows = 0;

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0), new ConnectionManager(dbHost, dbUser, dbPass)) {
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, service);
				stmt.setString(2, user.getIdFormat());
				stmt.setString(3, target.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();

		return (numAffectedRows == 1) ? true : false;
	}

	public static boolean removePermission(final String service, final User user, final User target) throws ServerException {
		String sqlRequest = "DELETE FROM `"+permissionTable+"` WHERE `service_id` = ? AND `user` = ? AND `granted_to` = ? LIMIT 1";

		int numAffectedRows = 0;

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, service);
				stmt.setString(2, user.getIdFormat());
				stmt.setString(3, target.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();

		return (numAffectedRows == 1) ? true : false;
	}
	
	public static boolean removeAllPermissions(final User from, final User target) throws ServerException {
		String sqlRequest = "DELETE FROM `"+permissionTable+"` WHERE `user` = ? AND `granted_to` = ?";
		
		int numAffectedRows = 0;
		
		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {

			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, from.getIdFormat());
				stmt.setString(2, target.getIdFormat());
			}
		};
		numAffectedRows += rf.execute();

		return (numAffectedRows > 0) ? true : false;
	}
	
	public static Collection<String> getPermissions(final User from, final User granted_to) throws ServerException {
		String sqlRequest = "SELECT `service_id` FROM `"+permissionTable+"` WHERE `user` = ? AND `granted_to` = ? ";

		QueryRequestHandler<Collection<String>> rf = new QueryRequestHandler<Collection<String>>(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {
			
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, from.getIdFormat());
				stmt.setString(2, granted_to.getIdFormat());
			}
			
			@Override
			public Collection<String> processResult(ResultSet result)
			throws SQLException {

				HashSet<String> services = new HashSet<String>();
				result.beforeFirst();

				while (result.next()) {
					services.add(result.getString("service_id"));
				}

				return services;
			}
		};

		return rf.execute();
	}
	
	public static void updatePosition(final User user, final double longitude, final double latitude, final double altitude) throws ServerException {
		if (user == null) throw new IllegalArgumentException("null user");
		
		//works if user is primary key
		String sqlRequest = "REPLACE INTO `"+positionTable+"` (`user`, `latitude`, `longitude`, `altitude`) VALUES (?, ?, ?, ?)";
		
		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {
			
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, user.getIdFormat());
				stmt.setDouble(2, latitude);
				stmt.setDouble(3, longitude);
				stmt.setDouble(4, altitude);
			}
		};
		
		rf.execute();
	}
	
	public static Collection<SocialPosition> getPositions(final Collection<User> users, long timeout) throws ServerException {
		if (users == null || users.size() == 0 || users.contains(null) || timeout < 0)
			throw new IllegalArgumentException();
		
		StringBuilder requestBuilder = new StringBuilder("SELECT `user`, `latitude`, `longitude`, `altitude`, `last_update` FROM `"+positionTable+"` WHERE (");
		for (int i = 0; i < users.size() - 1; i++) {
			requestBuilder.append("`user` = ? OR ");
		}
		requestBuilder.append("`user` = ?) AND ");
		requestBuilder.append("`last_update` > (SUBTIME(NOW(), SEC_TO_TIME(" + timeout + ")))");
		
		String sqlRequest = requestBuilder.toString();
		
		
		QueryRequestHandler<Collection<SocialPosition>> rf = new QueryRequestHandler<Collection<SocialPosition>>(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {

			@Override
			public void prepareStatement(PreparedStatement stmt)
					throws SQLException {
				
				int questionMarkIndex = 1;
				for (User u : users) {
					stmt.setString(questionMarkIndex, u.getIdFormat());
					
					questionMarkIndex++;
				}
			}

			@Override
			public Collection<SocialPosition> processResult(ResultSet result) throws SQLException, ServerException {

				HashSet<SocialPosition> out = new HashSet<SocialPosition>();
				
				result.beforeFirst();
				while (result.next()) {
					double latitude = result.getDouble("latitude");
					double longitude = result.getDouble("longitude");
					double altitude = result.getDouble("altitude");
					User user = new User(result.getString("user"));
					Timestamp timestamp = result.getTimestamp("last_update");
					
					Position position = new Position(latitude, longitude, altitude);
					out.add(new SocialPosition(user, position, timestamp));
				}
				
				return out;
			}
			
		};
		
		Collection<SocialPosition> out = rf.execute();
		
		return out;
	}
	
	
	public static Collection<User> getVisibleFriends(final User me, final String serviceId) throws ServerException {
		String sqlRequest = "SELECT `user` FROM `"+permissionTable+"` WHERE `granted_to` = ? AND `service_id` = ? ";

		QueryRequestHandler<Collection<User>> rf = new QueryRequestHandler<Collection<User>>(sqlRequest, new ConnectionManager(dbHost, dbUser, dbPass)) {
			
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, me.getIdFormat());
				stmt.setString(2, serviceId);
			}
			
			@Override
			public Collection<User> processResult(ResultSet result)
			throws SQLException {

				HashSet<User> services = new HashSet<User>();
				result.beforeFirst();

				while (result.next()) {
					services.add(new User(result.getString("user")));
				}

				return services;
			}
		};

		return rf.execute();
	}
}
