package org.pocketcampus.plugin.social;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import org.pocketcampus.core.database.handlers.exceptions.SQLIntegrityConstraintViolationExceptionHandler;
import org.pocketcampus.core.database.handlers.requests.CountRequestHandler;
import org.pocketcampus.core.database.handlers.requests.QueryRequestHandler;
import org.pocketcampus.core.database.handlers.requests.UpdateRequestHandler;
import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.shared.plugin.social.User;

public class SocialDatabase {
	private static final String contactTable = "social_contact";
	private static final String pendingTable = "social_pending";
	private static final String permissionTable = "social_permissions";
//	private static final String positionTable = "social_positions";

	/**
	 * @param a
	 * @param b
	 * @return true if a and b are friends already
	 */
	public static boolean testFriend(final User a, final User b) throws ServerException {
		String sqlRequest = "SELECT COUNT(*) AS `count` FROM `"+contactTable+"` WHERE `from` = ? AND `to` = ?";

		CountRequestHandler rf = new CountRequestHandler(sqlRequest, "count") {

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
		String sqlRequest = "INSERT INTO `"+contactTable+"` (`from`, `to`)" + " VALUES (?, ?)";
		int numAffectedRows = 0;

		UpdateRequestHandler rf1 = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0)) {
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, a.getIdFormat());
				stmt.setString(2, b.getIdFormat());
			}
		};
		numAffectedRows += rf1.execute();

		UpdateRequestHandler rf2 = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0)) {
			@Override
			public void prepareStatement(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, b.getIdFormat());
				stmt.setString(2, a.getIdFormat());
			}
		};
		numAffectedRows += rf2.execute();

		return (numAffectedRows == 2) ? true : false;
	}

	public static boolean removeFriend(final User a, final User b) throws ServerException {
		String sqlRequest = "DELETE FROM `"+contactTable+"` WHERE `from` = ? AND `to` = ? LIMIT 1";

		int numAffectedRows = 0;

		UpdateRequestHandler rf1 = new UpdateRequestHandler(sqlRequest) {

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

		QueryRequestHandler<Collection<User>> rf = new QueryRequestHandler<Collection<User>>(sqlRequest) {
			
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

		CountRequestHandler rf = new CountRequestHandler(sqlRequest, "count") {

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

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0)) {
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

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest) {

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

		QueryRequestHandler<Collection<User>> rf = new QueryRequestHandler<Collection<User>>(sqlRequest) {
			
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

		CountRequestHandler rf = new CountRequestHandler(sqlRequest, "count") {

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

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest, new SQLIntegrityConstraintViolationExceptionHandler(0)) {
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

		UpdateRequestHandler rf = new UpdateRequestHandler(sqlRequest) {

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
}
