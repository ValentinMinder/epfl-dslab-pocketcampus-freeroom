package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.thrift.TException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.exchange.ExchangeEntry;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRCourse;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * The actual implementation of the server side of the FreeRoom Plugin.
 * 
 * It responds to different types of request from the clients.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class FreeRoomServiceImpl implements FreeRoomService.Iface {
	// ********** START OF "INITIALIZATION" PART **********
	private final String URL_ROOMS_LIST = "https://pocketcampus.epfl.ch/proxy/"
			+ "archibus.php/rwsrooms/searchRooms"
			+ "?961264a174e15211109e1deb779b17d0=1&app=freeroom&"
			+ "caller=public&unitname=DAF%";
	private final String URL_INDIVIDUAL_ROOM = "http://pocketcampus.epfl.ch/proxy/"
			+ "archibus.php/rwsrooms/getRoom"
			+ "?961264a174e15211109e1deb779b17d0=1&app=freeroom&caller=sciper&id=";
	private final String FILE_DINCAT = "src/org/pocketcampus/plugin/freeroom/server/data/locaux_din.txt";
	private HashMap<String, String> dincat_text = null;

	private ConnectionManager connMgr;
	// margin for error is a minute
	private final long MARGIN_ERROR_TIMESTAMP = 60 * 1000;

	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ...");
		try {
			connMgr = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"),
					PC_SRV_CONFIG.getString("DB_PASSWORD"));
			fetchRoomsIntoDB();
		} catch (ServerException e) {
			e.printStackTrace();
		}
		// update ewa : should be done periodically...
		boolean updateEWA = false;
		if (updateEWA) {
			if (updateEWAOccupancy()) {
				System.out.println("EWA data succesfully updated!");
			} else {
				System.err.println("EWA data couldn't be completely loaded!");
			}
		}

	}

	// for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
	}

	// ********** END OF "INITIALIZATION" PART **********
	// ********** START OF "PUBLIC SERVER SERVICES" PART **********

	/**
	 * Search for all rooms available during the time period included in the
	 * request.
	 */
	@Override
	public FreeRoomReply getFreeRoomFromTime(FreeRoomRequest request)
			throws TException {
		// reduce the total duration to avoid having possibly exact same
		// timestamp
		FRPeriod period = Utils.convertMinPrecision(request).getPeriod();

		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		if (!FRTimes.validCalendars(period)) {
			// if something is wrong in the request
			// for security reasons, we don't tell the client was exactly was
			// wrong
			return new FreeRoomReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"Bad timestamps! Your client sent a bad request, sorry");
		}

		FreeRoomReply reply = new FreeRoomReply(HttpURLConnection.HTTP_OK, "");

		boolean recurrent = period.isRecurrent();

		if (!recurrent) {
			Set<FRRoom> rooms = getFreeRoom(tsStart, tsEnd);
			if (rooms != null) {
				reply.setRooms(rooms);
				reply.setRoomsIsSet(true);
			} else {
				reply = new FreeRoomReply(
						HttpURLConnection.HTTP_INTERNAL_ERROR,
						"Internal server error, sorry.");
			}
			return reply;
		} else {
			// TODO: support recurrent request
			reply = new FreeRoomReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					"reccurent request not supported yet");
			return reply;
		}
	}

	private Set<FRRoom> getFreeRoom(long start, long end) throws TException {

		HashSet<FRRoom> freerooms = new HashSet<FRRoom>();
		try {
			Connection connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD
					.prepareStatement("SELECT rl.doorCode, rl.uid "
							+ "FROM `fr-roomslist` rl "
							+ "WHERE rl.uid NOT IN "
							+ "(SELECT ro.uid FROM `fr-roomsoccupancy` ro "
							+ "WHERE ((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
							+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
							+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?)))");

			// filling the query with values
			query.setLong(1, end);
			query.setLong(2, start);
			query.setLong(3, end);
			query.setLong(4, start);
			query.setLong(5, start);
			query.setLong(6, end);

			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String uid = resultQuery.getString("uid");
				String doorCode = resultQuery.getString("doorCode");
				FRRoom r = new FRRoom(doorCode, uid);
				freerooms.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return freerooms;
	}

	@Override
	public OccupancyReply checkTheOccupancy(OccupancyRequest request)
			throws TException {

		OccupancyReply reply = new OccupancyReply(
				HttpURLConnection.HTTP_CREATED, ""
						+ HttpURLConnection.HTTP_CREATED);

		List<String> uidsList = request.getUids();

		FRPeriod period = request.getPeriod();
		long timestampStart = period.getTimeStampStart();
		long timestampEnd = period.getTimeStampEnd();

		if (!FRTimes.validCalendars(period)) {
			// if something is wrong in the request
			return new OccupancyReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"Bad timestamps! Your client sent a bad request, sorry");
		}

		ArrayList<Occupancy> occupancies = new ArrayList<Occupancy>();

		try {
			Connection connectBDD = connMgr.getConnection();

			for (String mUid : uidsList) {
				Occupancy mOccupancy = new Occupancy();

				PreparedStatement roomQuery = connectBDD
						.prepareStatement("SELECT "
								+ "rl.doorCode, rl.capacity "
								+ "FROM `fr-roomslist` rl "
								+ "WHERE rl.uid = ? ");
				roomQuery.setString(1, mUid);
				ResultSet resultRoom = roomQuery.executeQuery();

				FRRoom room = null;
				if (resultRoom.next()) {
					room = new FRRoom();
					room.setUid(mUid);
					room.setDoorCode(resultRoom.getString("doorCode"));
					room.setCapacity(resultRoom.getInt("capacity"));
					// room.setType(FRRoomType.valueOf(resultRoom
					// .getString("type")));
					if (resultRoom.next()) {
						return new OccupancyReply(
								HttpURLConnection.HTTP_INTERNAL_ERROR,
								"Mutltiple rooms with same UID! Error!");
					}
					mOccupancy.setRoom(room);
				} else {
					return new OccupancyReply(
							HttpURLConnection.HTTP_BAD_REQUEST,
							"Unknown room UID, sorry");
				}

				PreparedStatement query = connectBDD
						.prepareStatement("SELECT ro.timestampStart, ro.timestampEnd, "
								+ "rl.doorCode "
								+ "FROM `fr-roomsoccupancy` ro, `fr-roomslist` rl "
								+ "WHERE rl.uid = ? "
								+ "AND ro.uid = rl.uid AND "
								+ "((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
								+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
								+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?)) "
								+ "ORDER BY ro.timestampStart ASC");
				query.setString(1, mUid);
				query.setLong(2, timestampEnd);
				query.setLong(3, timestampStart);
				query.setLong(4, timestampEnd);
				query.setLong(5, timestampStart);
				query.setLong(6, timestampStart);
				query.setLong(7, timestampEnd);

				// filling the query with values

				ResultSet resultQuery = query.executeQuery();

				boolean isAtLeastOccupiedOnce = false;
				boolean isAtLeastFreeOnce = false;

				// timestamp used to generate the occupations accross the
				// FRPeriod
				long tsPerRoom = timestampStart;
				while (resultQuery.next()) {

					long tsStart = Math.max(tsPerRoom,
							resultQuery.getLong("timestampStart"));
					long tsEnd = Math.min(timestampEnd,
							resultQuery.getLong("timestampEnd"));

					if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
						// We got a free period of time !
						ActualOccupation mOcc = new ActualOccupation();
						FRPeriod myPeriod = new FRPeriod(tsPerRoom,
								tsStart - 1, false);
						mOcc.setPeriod(myPeriod);
						mOcc.setAvailable(true);
						mOcc.setProbableOccupation(getWorstCaseUserOccupancy(
								myPeriod, room));
						mOccupancy.addToOccupancy(mOcc);
						isAtLeastFreeOnce = true;
					}

					ActualOccupation mAccOcc = new ActualOccupation();
					mAccOcc.setPeriod(new FRPeriod(tsStart, tsEnd, false));
					mAccOcc.setAvailable(false);
					mOccupancy.addToOccupancy(mAccOcc);
					isAtLeastOccupiedOnce = true;

					tsPerRoom = tsEnd;

				}

				// There is some free time left after the last result
				if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
					ActualOccupation mOcc = new ActualOccupation();
					FRPeriod myPeriod = new FRPeriod(tsPerRoom, timestampEnd,
							false);
					mOcc.setPeriod(myPeriod);
					mOcc.setAvailable(true);
					mOcc.setProbableOccupation(getWorstCaseUserOccupancy(
							myPeriod, room));
					mOccupancy.addToOccupancy(mOcc);
					isAtLeastFreeOnce = true;
				}

				mOccupancy.setIsAtLeastFreeOnce(isAtLeastFreeOnce);
				mOccupancy.setIsAtLeastOccupiedOnce(isAtLeastOccupiedOnce);

				occupancies.add(mOccupancy);
				query.close();
			}

			reply = new OccupancyReply(HttpURLConnection.HTTP_OK, ""
					+ HttpURLConnection.HTTP_OK);
			reply.setOccupancyOfRooms(occupancies);
		} catch (SQLException e) {
			reply = new OccupancyReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					"" + HttpURLConnection.HTTP_INTERNAL_ERROR);
			e.printStackTrace();
		}

		return reply;
	}

	/**
	 * Returns all the rooms that satisfies the hint given in the request.
	 * 
	 * The hint may be the start of the door code or the uid.
	 * 
	 * TODO: verifies that it works with PH D2 398, PHD2 398, PH D2398 and
	 * PHD2398
	 * 
	 * TODO: limit the number of result given
	 */
	@Override
	public AutoCompleteReply autoCompleteRoom(AutoCompleteRequest request)
			throws TException {
		AutoCompleteReply reply = new AutoCompleteReply(
				HttpURLConnection.HTTP_CREATED, ""
						+ HttpURLConnection.HTTP_CREATED);

		List<FRRoom> rooms = new ArrayList<FRRoom>();
		Set<String> forbiddenRooms = request.getForbiddenRoomsUID();

		String forbidRoomsSQL = "";
		if (forbiddenRooms != null) {
			for (int i = forbiddenRooms.size(); i > 0; --i) {
				if (i <= 1) {
					forbidRoomsSQL += "?";
				} else {
					forbidRoomsSQL += "?,";
				}
			}
		}
		String txt = request.getConstraint();
		// avoid all whitespaces for requests
		// TODO: be resistent to empty queries!
		// put a minimum number of letters for the hint
		// this is only for tests purposes, to deliver all the rooms
		txt = txt.trim();
		txt = txt.replaceAll("\\s", "");
		try {
			Connection connectBDD = connMgr.getConnection();
			String requestSQL = "";
			if (forbiddenRooms == null) {
				requestSQL = "SELECT * " + "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCode LIKE (?)) "
						+ "ORDER BY rl.doorCode ASC";
			} else {
				requestSQL = "SELECT * " + "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCode LIKE (?)) "
						+ "AND rl.uid NOT IN (" + forbidRoomsSQL + ") "
						+ "ORDER BY rl.doorCode ASC";
			}

			PreparedStatement query = connectBDD.prepareStatement(requestSQL);
			query.setString(1, txt + "%");
			query.setString(2, txt + "%");

			if (forbiddenRooms != null) {
				int i = 2;
				for (String roomUID : forbiddenRooms) {
					query.setString(i, roomUID);
					++i;
				}
			}

			// filling the query with values

			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				FRRoom frRoom = new FRRoom(resultQuery.getString("doorCode"),
						resultQuery.getString("uid"));
				// String type = resultQuery.getString("type");
				// if (type != null) {
				// try {
				// FRRoomType t = FRRoomType.valueOf(type);
				// frRoom.setType(t);
				// } catch (IllegalArgumentException e) {
				// System.err.println("Type not known " + type);
				// e.printStackTrace();
				// }
				// }
				int cap = resultQuery.getInt("capacity");
				if (cap > 0) {
					frRoom.setCapacity(cap);
				}
				rooms.add(frRoom);

			}
			reply = new AutoCompleteReply(HttpURLConnection.HTTP_OK, ""
					+ HttpURLConnection.HTTP_OK);
			reply.setListFRRoom(rooms);

		} catch (SQLException e) {
			reply = new AutoCompleteReply(
					HttpURLConnection.HTTP_INTERNAL_ERROR, ""
							+ HttpURLConnection.HTTP_INTERNAL_ERROR);
			e.printStackTrace();
		}
		return reply;
	}

	/**
	 * Register that a user will be in a room for a given time.
	 */
	@Override
	public ImWorkingReply indicateImWorking(ImWorkingRequest request)
			throws TException {
		ImWorkingReply reply = new ImWorkingReply(
				HttpURLConnection.HTTP_INTERNAL_ERROR, "");

		try {
			Connection connectBDD = connMgr.getConnection();
			WorkingOccupancy w = request.getWork();
			List<FRPeriod> mFrPeriods = FRTimes
					.getFRPeriodByStep(w.getPeriod());
			int size = mFrPeriods.size();
			FRRoom mFrRoom = w.getRoom();
			String roomUID = mFrRoom.getUid();

			// get the previously registered user occupancies
			List<Integer> listUserOccupancy = getUserOccupancy(mFrPeriods,
					mFrRoom);

			// construct the query
			String line = "UPDATE `fr-usersoccupancy` "
					+ "SET count = (?) "
					+ "WHERE uid = (?) AND timestampStart = (?) AND timestampEnd = (?); \n";
			StringBuilder build = new StringBuilder(line.length() * size);
			for (int i = 0; i < size; i++) {
				build.append(line);
			}
			PreparedStatement query = connectBDD.prepareStatement(build
					.toString());

			// put the values in the query.
			for (int i = 0, j = 0; i < size; i++, j = 4 * i) {
				query.setInt(j + 1, (listUserOccupancy.get(i).intValue() + 1));
				query.setString(j + 2, roomUID);
				FRPeriod period = mFrPeriods.get(i);
				query.setLong(j + 3, period.getTimeStampStart());
				query.setLong(j + 4, period.getTimeStampEnd());
			}

			System.out.println(query.toString());
			query.execute();

			// checks if advanced mode is needed for this request.
			boolean updateUsersWorking = w.isSetCourse() || w.isSetMessage();
			boolean resultAdvanced = true;
			if (updateUsersWorking) {
				resultAdvanced = indicateImWorkingAdvanced(request);
			}

			if (resultAdvanced) {
				reply = new ImWorkingReply(HttpURLConnection.HTTP_OK,
						"ImWorking set + advanced if any set");
			} else {
				// TODO: no differenciation ?
				reply = new ImWorkingReply(
						HttpURLConnection.HTTP_INTERNAL_ERROR,
						"FAIL: advanced working occupancy failed (but usual one worked)");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return reply;
	}

	/**
	 * Sets the advanced imWorkingRequest in the appropriate DB.
	 * 
	 * @param request
	 * @return
	 */
	private boolean indicateImWorkingAdvanced(ImWorkingRequest request) {
		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			WorkingOccupancy workingOccupancy = request.getWork();
			String roomUID = workingOccupancy.getRoom().getUid();

			// construct the query
			String SQL = "INSERT INTO `fr-usersworking` (`userID`, `timestampStart`, `timestampEnd`, `course_id`, `course_name`, `message` ,`uid` ) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement queryUsersWorking = connectBDD
					.prepareStatement(SQL);
			// TODO: the user has to send is sciper EPFL ID!
			queryUsersWorking.setInt(1, 123456);
			FRPeriod period = workingOccupancy.getPeriod();
			queryUsersWorking.setLong(2, period.getTimeStampStart());
			queryUsersWorking.setLong(3, period.getTimeStampEnd());

			// if any, set the course details
			if (workingOccupancy.isSetCourse()) {
				queryUsersWorking.setString(4, workingOccupancy.getCourse()
						.getCourseID());
				queryUsersWorking.setString(5, workingOccupancy.getCourse()
						.getCourseName());
			} else {
				queryUsersWorking.setString(4, null);
				queryUsersWorking.setString(5, null);
			}

			// if any, set the personalized message.
			if (workingOccupancy.isSetMessage()) {
				queryUsersWorking.setString(6, workingOccupancy.getMessage());
			} else {
				queryUsersWorking.setString(6, null);
			}

			// set the roomID and execute the query
			queryUsersWorking.setString(7, roomUID);
			queryUsersWorking.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Returns all the rooms in the DB.
	 * 
	 * @return a collection of Rooms.
	 */
	private Collection<FRRoom> getAllRoomsInDB() {
		Connection connectBDD;
		try {
			// construct the query
			connectBDD = connMgr.getConnection();
			String reqSQL = "SELECT rl.doorCode, rl.uid "
					+ "FROM `fr-roomslist` rl";
			PreparedStatement query = connectBDD.prepareStatement(reqSQL);

			// retrieves the result
			Collection<FRRoom> collection = new ArrayList<FRRoom>(400);
			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String uid = resultQuery.getString("uid");
				String doorCode = resultQuery.getString("doorCode");
				FRRoom r = new FRRoom(doorCode, uid);
				collection.add(r);
			}
			return collection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Should be called for TESTS, to truncate the table and initiate a new one
	 * with empty values.
	 * 
	 * @return
	 */
	public boolean initUserOccupation() {
		try {
			// truncate DB
			Connection connectBDD = connMgr.getConnection();
			String resSQL = "TRUNCATE `fr-usersoccupancy`";
			PreparedStatement query = connectBDD.prepareStatement(resSQL);
			query.execute();

			long start = System.currentTimeMillis() - FRTimes.MAX_TIME_IN_PAST;
			long end = System.currentTimeMillis() + FRTimes.MAX_TIME_IN_FUTURE
					+ FRTimes.AUTO_UPDATE_INTERVAL_USER_OCCUPANCY
					- FRTimes.USER_OCCUPANCY_UPDATE_MARGIN;
			start -= FRTimes.USER_OCCUPANCY_UPDATE_MARGIN;
			FRPeriod period = new FRPeriod(start, end, false);
			return fillDBWithEmptyUserOccupationAllRoomsFixedPeriod(period);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Fills an empty user occupation for every step in pre-defined period, for
	 * every room in DB.
	 * 
	 * Should be called periodically, every
	 * <code>FRTimes.AUTO_UPDATE_INTERVAL_USER_OCCUPANCY</code>
	 * 
	 * (like, every day at midnight)
	 * 
	 * @return
	 */
	private boolean fillDBWithEmpytUserOccupationAllRoomsAutoDefinedPeriod() {
		// TODO: to avoid conflict, this method should check the last registered
		// timestamp before inserting new ones (if same timestamp, we have an
		// error as it's primary key)
		long start = System.currentTimeMillis() + FRTimes.MAX_TIME_IN_FUTURE
				+ FRTimes.AUTO_UPDATE_INTERVAL_USER_OCCUPANCY;
		long end = start + FRTimes.AUTO_UPDATE_INTERVAL_USER_OCCUPANCY
				- FRTimes.USER_OCCUPANCY_UPDATE_MARGIN;
		start -= FRTimes.USER_OCCUPANCY_UPDATE_MARGIN;
		FRPeriod period = new FRPeriod(start, end, false);
		return fillDBWithEmptyUserOccupationAllRoomsFixedPeriod(period);
	}

	/**
	 * Fills an empty user occupation for every step in the period, for every
	 * room in DB.
	 * 
	 * @param period
	 * @return
	 */
	private boolean fillDBWithEmptyUserOccupationAllRoomsFixedPeriod(
			FRPeriod period) {
		boolean flag = true;
		Iterator<FRRoom> iter = getAllRoomsInDB().iterator();
		boolean result = true;
		while (iter.hasNext()) {
			result = fillDBWithEmptyUserOccupationFixedRoomFixedPeriod(period,
					iter.next());
			flag = flag && result;
		}
		return flag;
	}

	/**
	 * Fills an empty user occupation for every step in the period, for the
	 * given room.
	 * 
	 * @param mFrPeriod
	 * @param mFrRoom
	 * @return
	 */
	private boolean fillDBWithEmptyUserOccupationFixedRoomFixedPeriod(
			FRPeriod mFrPeriod, FRRoom mFrRoom) {
		try {
			Connection connectBDD = connMgr.getConnection();
			List<FRPeriod> mFrPeriods = FRTimes.getFRPeriodByStep(mFrPeriod);
			int size = mFrPeriods.size();
			String roomUID = mFrRoom.getUid();
			String first = "INSERT INTO `fr-usersoccupancy` "
					+ "(uid, timestampStart, timestampEnd, count) VALUES ";
			String line = "((?), (?), (?), (?)), ";

			StringBuilder build = new StringBuilder(first.length()
					+ line.length() * size);
			build.append(first);
			for (int i = 0; i < size - 1; i++) {
				build.append(line);
			}
			build.append("((?), (?), (?), (?))");
			PreparedStatement query = connectBDD.prepareStatement(build
					.toString());

			for (int i = 0, j = 0; i < size; i++, j = 4 * i) {
				query.setString(j + 1, roomUID);
				FRPeriod period = mFrPeriods.get(i);
				query.setLong(j + 2, period.getTimeStampStart());
				query.setLong(j + 3, period.getTimeStampEnd());
				query.setInt(j + 4, 0);
			}

			query.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getWorstCaseUserOccupancy(FRPeriod mFrPeriod, FRRoom mFrRoom) {
		List<Integer> listInt = getUserOccupancy(mFrPeriod, mFrRoom);
		int max = 0;
		for (Integer integer : listInt) {
			int myValue = integer.intValue();
			if (myValue > max) {
				max = myValue;
			}
		}
		return max;
	}

	public List<Integer> getUserOccupancy(FRPeriod mFrPeriod, FRRoom mFrRoom) {
		List<FRPeriod> mFrPeriods = FRTimes.getFRPeriodByStep(mFrPeriod);
		return getUserOccupancy(mFrPeriods, mFrRoom);
	}

	/**
	 * Return the count occupancy of the given FRperiod, for the given room.
	 * 
	 * FRPeriod MUST been "stepped" before using the utility method in
	 * <code>FRTimes</code>
	 * 
	 * @param mFRperiods
	 * @param mFrRoom
	 * @return
	 */
	private List<Integer> getUserOccupancy(List<FRPeriod> mFRperiods,
			FRRoom mFrRoom) {
		int size = mFRperiods.size();
		if (size < 1) {
			String error = this.getClass().getSimpleName()
					+ ": getUserOccupancy must have at least one mFRPeriod";
			System.err.println(error);
			return null;
		}

		try {
			Connection connectionDB = connMgr.getConnection();

			// preparing the query
			String firstWord = "SELECT `count` FROM `fr-usersoccupancy` WHERE `uid`=(?) AND ( ";
			String rowWord = "timestampStart=(?) ";
			String orWord = "OR ";
			String lastWord = ") ORDER BY timestampStart";
			StringBuilder builder = new StringBuilder(firstWord.length()
					+ lastWord.length() + (rowWord.length() + orWord.length())
					* size);
			builder.append(firstWord);
			for (int i = 0; i < size - 1; i++) {
				builder.append(rowWord);
				builder.append(orWord);
			}
			builder.append(rowWord);
			builder.append(lastWord);

			// filling the query with values.
			PreparedStatement query = connectionDB.prepareStatement(builder
					.toString());
			query.setString(1, mFrRoom.getUid());
			for (int i = 0; i < size; i++) {
				query.setLong(i + 2, mFRperiods.get(i).getTimeStampStart());
			}

			// executing the query
			ResultSet resultQuery = query.executeQuery();
			List<Integer> listInteger = new ArrayList<Integer>(size);
			while (resultQuery.next()) {
				listInteger.add(new Integer(resultQuery.getInt("count")));
			}

			if (listInteger.size() != size) {
				String error = "Method not consistant! "
						+ "Must return exactly the same number of occupancy than FRPeriod given";
				System.err.println(error);
				return null;
			}
			return listInteger;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves who is working according to some constraints.
	 * 
	 * TODO: take the constraints into account (room, time, message)! For now,
	 * it returns everything!
	 */
	@Override
	public WhoIsWorkingReply whoIsWorking(WhoIsWorkingRequest request)
			throws TException {

		WhoIsWorkingReply reply = new WhoIsWorkingReply(
				HttpURLConnection.HTTP_INTERNAL_ERROR, "");
		Connection connectionDB;
		try {
			connectionDB = connMgr.getConnection();
			String SQL = "SELECT * FROM `fr-usersworking`";
			PreparedStatement query = connectionDB.prepareStatement(SQL);

			// executing the query
			List<WorkingOccupancy> theyAreWorking = new ArrayList<WorkingOccupancy>();
			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String uid = resultQuery.getString("uid");
				long timestampStart = resultQuery.getLong("timestampStart");
				long timestampEnd = resultQuery.getLong("timestampEnd");
				FRPeriod period = new FRPeriod(timestampStart, timestampEnd,
						false);
				// TODO: how to get the door code from the other table ?
				FRRoom room = new FRRoom("fake door code", uid);
				WorkingOccupancy workingOccupancy = new WorkingOccupancy(
						period, room);
				String courseID = resultQuery.getString("course_id");

				String courseName = resultQuery.getString("course_name");
				if (courseID != null && courseName != null) {
					FRCourse course = new FRCourse(courseID, courseName);
					workingOccupancy.setCourse(course);
				}
				String message = resultQuery.getString("message");
				if (message != null) {
					workingOccupancy.setMessage(message);
				}

				theyAreWorking.add(workingOccupancy);
			}
			reply = new WhoIsWorkingReply(HttpURLConnection.HTTP_OK, "");
			reply.setTheyAreWorking(theyAreWorking);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return reply;
	}

	// ********** END OF "PUBLIC SERVER SERVICES" PART **********
	// ********** START OF "FETCHING ROOMS DATA FROM ARCHIBUS" PART **********

	public int fetchRoomsIntoDB() {
		int totalCount = 0;
		StringBuffer page = new StringBuffer();
		URL url;
		InputStream is = null;
		BufferedReader bufferedInput;

		try {
			url = new URL(URL_ROOMS_LIST);
			is = url.openStream();
			bufferedInput = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = bufferedInput.readLine()) != null) {
				page.append(line);
			}

			totalCount += fetchAndinsertRoomsDetailsFromJSONtoDB(page);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return totalCount;
	}

	/**
	 * Fetch details about a room (i.e all its attributes) and convert it to a
	 * JSONObject.
	 * 
	 * @param uid
	 *            The unique id of the room to fetch.
	 * @return The JSONObject associated or null if an error occured (wrong id,
	 *         error...)
	 */
	public JSONObject fetchRoomDetail(String uid) {
		StringBuffer mRoomDetail = new StringBuffer();
		URL url;
		InputStream is = null;
		BufferedReader bufferedInput;
		JSONObject mJSONRoom = null;

		try {
			url = new URL(URL_INDIVIDUAL_ROOM + uid);
			is = url.openStream();
			bufferedInput = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = bufferedInput.readLine()) != null) {
				mRoomDetail.append(line);
			}

			mJSONRoom = new JSONObject(mRoomDetail.toString());

			if (mJSONRoom.has("result")) {
				return mJSONRoom.getJSONObject("result");
			}

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int fetchAndinsertRoomsDetailsFromJSONtoDB(StringBuffer page) {
		int countInsert = 0;

		try {
			JSONObject jsonPage = new JSONObject(page.toString());
			JSONArray arrayPage = jsonPage.getJSONArray("result");

			int numberOfRecord = arrayPage.length();
			for (int i = 0; i < numberOfRecord; ++i) {
				JSONObject record = arrayPage.getJSONObject(i);
				String uid = record.getString("id");

				JSONObject roomDetail = fetchRoomDetail(uid);

				if (roomDetail != null) {
					if (insertIntoDBRoomDetail(roomDetail)) {
						countInsert++;
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return countInsert;
	}

	private boolean insertIntoDBRoomDetail(JSONObject room) {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		String req = "INSERT INTO `fr-roomslist`("
				+ "uid, doorCode, doorCodeWithoutSpace, capacity, "
				+ "site_label, surface, building_name, zone, unitlabel, "
				+ "site_id, floor, unitname, site_name, unitid, building_label, "
				+ "cf, adminuse, type, dincat) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?, ?, ?, ?, ?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(req);
			// filling the query with values
			// doorCode is mapped to name in the JSON
			if (room.has("id") && room.has("name")) {
				query.setString(1, room.getString("id"));
				query.setString(2, room.getString("name"));
				query.setString(3, room.getString("name").replaceAll("\\s", ""));
			} else {
				// the required field for inserting a new record in the DB are
				// not available,
				// useless to go further
				return false;
			}

			// from now, fields are optional, thus if some are not present,
			// we still continue to check the other

			if (room.has("places")) {
				query.setInt(4, room.getInt("places"));
			} else {
				query.setNull(4, Types.INTEGER);
			}

			if (room.has("site_label")) {
				query.setString(5, room.getString("site_label"));
			} else {
				query.setNull(5, Types.CHAR);
			}

			if (room.has("surface")) {
				query.setDouble(6, room.getDouble("surface"));
			} else {
				query.setNull(6, Types.DOUBLE);
			}

			if (room.has("building_name")) {
				query.setString(7, room.getString("building_name"));
			} else {
				query.setNull(7, Types.CHAR);
			}

			if (room.has("zone")) {
				query.setString(8, room.getString("zone"));
			} else {
				query.setNull(8, Types.CHAR);
			}

			if (room.has("unitlabel")) {
				query.setString(9, room.getString("unitlabel"));
			} else {
				query.setNull(9, Types.CHAR);
			}

			if (room.has("site_id")) {
				query.setInt(10, room.getInt("site_id"));
			} else {
				query.setNull(10, Types.INTEGER);
			}

			if (room.has("floor")) {
				query.setInt(11, room.getInt("floor"));
			} else {
				query.setNull(11, Types.INTEGER);
			}

			if (room.has("unitname")) {
				query.setString(12, room.getString("unitname"));
			} else {
				query.setNull(12, Types.CHAR);
			}

			if (room.has("site_name")) {
				query.setString(13, room.getString("site_name"));
			} else {
				query.setNull(13, Types.CHAR);
			}

			if (room.has("unitid")) {
				query.setInt(14, room.getInt("unitid"));
			} else {
				query.setNull(14, Types.INTEGER);
			}

			if (room.has("building_label")) {
				query.setString(15, room.getString("building_label"));
			} else {
				query.setNull(15, Types.CHAR);
			}

			if (room.has("cf")) {
				query.setString(16, room.getString("cf"));
			} else {
				query.setNull(16, Types.CHAR);
			}

			if (room.has("adminuse")) {
				query.setString(17, room.getString("adminuse"));
			} else {
				query.setNull(17, Types.CHAR);
			}

			if (room.has("dincat")) {
				String type = getFromFileDinCatString(room.getString("dincat"));
				query.setString(18, type);
				query.setString(19, room.getString("dincat"));
			} else {
				query.setNull(18, Types.CHAR);
				query.setNull(19, Types.CHAR);
			}

			query.executeUpdate();
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private String getFromFileDinCatString(String dincat) {
		if (dincat_text == null) {
			dincat_text = new HashMap<String, String>();
			try {
				Scanner sc = new Scanner(new File(FILE_DINCAT));

				while (sc.hasNext()) {
					String line = sc.nextLine();
					String[] lineSplitted = line.split(";");
					if (lineSplitted.length >= 2) {
						dincat_text.put(lineSplitted[0], lineSplitted[1]);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return dincat_text.get(dincat);

	}

	// ********** END OF "FETCHING ROOMS DATA FROM ARCHIBUS" PART **********
	// ********** START OF "FETCHING EXCHANGE" PART **********

	/**
	 * Reset all the exchange ids to NULL.
	 * 
	 * @return
	 */
	private boolean resetExchangeData() {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
			PreparedStatement query;
			String b = "UPDATE `fr-roomsoccupancy` SET EWAid = NULL WHERE *";
			query = conn.prepareStatement(b);
			query.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Set the exchange ID of the room represented by it's doorCodeWithoutSpace.
	 * 
	 * @param concatName
	 * @param ewaID
	 * @return
	 */
	public boolean setExchangeData(String concatName, String ewaID) {
		// checks that the room exists, and exist only once!
		if (getUIDFromDoorCode(concatName) == null) {
			return false;
		}
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		String req = "UPDATE `fr-roomslist`" + "SET EWAid = (?) "
				+ "WHERE doorCodeWithoutSpace = (?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(req);
			query.setString(1, ewaID);
			query.setString(2, concatName);
			query.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Works with doorcode or doorCodeWithoutSpace!
	 * 
	 * MUST be used with great caution! Only the uid is garantueed to be unique,
	 * this function returns a result when there is only one and exactly one
	 * match!
	 * 
	 * @param doorCode
	 * @return
	 */
	private String getUIDFromDoorCode(String doorCode) {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
			PreparedStatement roomQuery = conn.prepareStatement("SELECT "
					+ "rl.uid " + "FROM `fr-roomslist` rl "
					+ "WHERE rl.doorCode = ? OR rl.doorCodeWithoutSpace = ?");
			roomQuery.setString(1, doorCode);
			roomQuery.setString(2, doorCode);
			ResultSet result = roomQuery.executeQuery();
			if (result.next()) {
				String uid = result.getString("uid");
				if (result.next()) {
					System.err.println("Mutiple rooms found for door code:"
							+ doorCode);
					return null;
				}
				return uid;
			} else {
				return null;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * List all the rooms that have an EWAid set.
	 * 
	 * @return
	 */
	private List<FRRoom> getEWARooms() {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
			PreparedStatement roomQuery = conn.prepareStatement("SELECT "
					+ "rl.uid, rl.doorCode, rl.EWAid "
					+ "FROM `fr-roomslist` rl " + "WHERE EWAid IS NOT NULL");
			List<FRRoom> listEWARooms = new ArrayList<FRRoom>();
			ResultSet result = roomQuery.executeQuery();
			while (result.next()) {
				String uid = result.getString("uid");
				String doorCode = result.getString("doorCode");
				String EWAid = result.getString("EWAid");
				FRRoom room = new FRRoom(doorCode, uid);
				room.setEWAid(EWAid);
				listEWARooms.add(room);
			}
			return listEWARooms;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the occupancies from Exchange for all the rooms that have an
	 * EWAid set. It calls updateEWAOccupancy with a default time window.
	 * 
	 * @return true if successful for all the rooms, false if an error occured.
	 */
	public boolean updateEWAOccupancy() {
		// TODO: for now, we update from now to one week
		// to be set to same window as permitted by server and clients
		long timeStampStart = System.currentTimeMillis()
				- FRTimes.ONE_WEEK_IN_MS;
		long timeStampEnd = System.currentTimeMillis() + FRTimes.ONE_WEEK_IN_MS;
		FRPeriod mFrPeriod = new FRPeriod(timeStampStart, timeStampEnd, false);
		return updateEWAOccupancy(mFrPeriod);
	}

	/**
	 * Retrieves the occupancies from Exchange, for all the rooms that have an
	 * EWAid set. It's done for a given time window given by mFRPeriod.
	 * 
	 * @param mFrPeriod
	 *            the time window to check.
	 * @return true if successful for all the rooms, false if an error occurred.
	 */
	private boolean updateEWAOccupancy(FRPeriod mFrPeriod) {
		// TODO: it construct an exchange client with default username/login
		ExchangeEntry ee = new ExchangeEntry();

		List<FRRoom> listRooms = getEWARooms();
		Iterator<FRRoom> iter = listRooms.iterator();
		while (iter.hasNext()) {
			FRRoom room = iter.next();
			String uid = room.getUid();
			deleteAllOccupancies(uid);
			List<FRPeriod> occupied = ee.getAvailabilityFromEWAUID(
					room.getEWAid(), mFrPeriod);
			int length = occupied.size();
			if (length != 0) {
				Connection conn = null;
				try {
					conn = connMgr.getConnection();
					PreparedStatement query;
					StringBuilder b = new StringBuilder(
							"INSERT INTO `fr-roomsoccupancy`("
									+ "uid, timestampStart, timeStampEnd) "
									+ "VALUES(?, ?, ?)");
					for (int i = 1; i < length; i++) {
						b.append(",(?, ?, ?)");
					}
					query = conn.prepareStatement(b.toString());

					for (int i = 0, j = 0; i < length; i++, j = 3 * i) {
						FRPeriod mPeriod = occupied.get(i);
						query.setString(j + 1, uid);
						query.setLong(j + 2, mPeriod.getTimeStampStart());
						query.setLong(j + 3, mPeriod.getTimeStampEnd());
					}
					query.execute();
				} catch (SQLException e1) {
					e1.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Deletes all the occupancies for a given room.
	 * 
	 * @param uid
	 *            the uid of the room
	 * @return true if no error occured
	 */
	private boolean deleteAllOccupancies(String uid) {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
			PreparedStatement query;
			String b = "DELETE FROM `fr-roomsoccupancy` WHERE uid = ?";
			query = conn.prepareStatement(b);
			query.setString(1, uid);
			query.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	// ********** END OF "FETCHING EXCHANGE" PART **********

}
