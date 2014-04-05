package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.exchange.ExchangeServiceImpl;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRCourse;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
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

	private final int LIMIT_AUTOCOMPLETE = 50;
	private ConnectionManager connMgr;
	private ExchangeServiceImpl mExchangeService;

	// margin for error is a minute
	private final long MARGIN_ERROR_TIMESTAMP = 60 * 1000;

	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ...");
		try {
			connMgr = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL")
					+ "?allowMultiQueries=true",
					PC_SRV_CONFIG.getString("DB_USERNAME"),
					PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			e.printStackTrace();
		}

		mExchangeService = new ExchangeServiceImpl(
				PC_SRV_CONFIG.getString("DB_URL") + "?allowMultiQueries=true",
				PC_SRV_CONFIG.getString("DB_USERNAME"),
				PC_SRV_CONFIG.getString("DB_PASSWORD"));

		// update ewa : should be done periodically...
		boolean updateEWA = true;
		if (updateEWA) {
			if (mExchangeService.updateEWAOccupancy()) {
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

	// START OF METHODS TO IMPLEMENTS

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

	@Override
	public FRReply getOccupancy(FRRequest request) throws TException {
		/*
		 * struct FRRequest { 1: required FRPeriod period; 2: required bool
		 * onlyFreeRooms; //if null, it means every rooms 3: required
		 * list<string> uidList; }
		 * 
		 * struct FRReply { 1: required i32 status; 2: required string
		 * statusComment; //map from building to list of occupancies in the
		 * building 3: optional map<string, list<Occupancy>> occupancyOfRooms; }
		 */

		FRReply reply = new FRReply(HttpURLConnection.HTTP_CREATED,
				HttpURLConnection.HTTP_CREATED + "");

		FRPeriod period = request.getPeriod();

		if (!FRTimes.validCalendars(period)) {
			// if something is wrong in the request
			return new FRReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"Bad timestamps! Your client sent a bad request, sorry");
		}

		boolean onlyFreeRoom = request.isOnlyFreeRooms();
		List<String> uidList = request.getUidList();

		HashMap<String, List<Occupancy>> occupancies = null;

		if (uidList == null) {
			// we want to look into all the rooms
			occupancies = getOccupancyOfAnyFreeRoom(onlyFreeRoom,
					period.getTimeStampStart(), period.getTimeStampEnd());
		} else {
			// or the user specified a specific list of rooms he wants to check
			occupancies = getOccupancyOfSpecificRoom(uidList, onlyFreeRoom);
		}

		reply.setOccupancyOfRooms(occupancies);
		return reply;
	}

	private HashMap<String, List<Occupancy>> getOccupancyOfAnyFreeRoom(
			boolean onlyFreeRooms, long tsStart, long tsEnd) {
		HashMap<String, List<Occupancy>> result = new HashMap<String, List<Occupancy>>();
		if (onlyFreeRooms) {
			String request = "SELECT rl.doorCode, rl.uid , uo.count, rl.capacity, "
					+ tsStart
					+ " AS timestampStart, "
					+ tsEnd
					+ " AS timestampEnd "
					+ "FROM `fr-roomslist` rl "
					+ "LEFT OUTER JOIN `fr-usersoccupancy` uo "
					+ "ON uo.uid = rl.uid AND uo.timestampStart >= ? AND uo.timestampEnd >= ? "
					+ "WHERE rl.uid NOT IN "
					+ "(SELECT ro.uid FROM `fr-roomsoccupancy` ro "
					+ "WHERE ((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
					+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
					+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?))) "
					+ "ORDER BY rl.uid ASC";

			Connection connectBDD;
			try {
				connectBDD = connMgr.getConnection();
				PreparedStatement query = connectBDD.prepareStatement(request);
				query.setLong(1, tsStart);
				query.setLong(2, tsEnd);
				query.setLong(3, tsEnd);
				query.setLong(4, tsStart);
				query.setLong(5, tsEnd);
				query.setLong(6, tsStart);
				query.setLong(7, tsStart);
				query.setLong(8, tsEnd);

				ResultSet resultQuery = query.executeQuery();

				while (resultQuery.next()) {
					//extract attributes of record
					long start = resultQuery.getLong("timestampStart");
					long end = resultQuery.getLong("timestampEnd");
					String uid = resultQuery.getString("uid");
					String doorCode = resultQuery.getString("doorCode");
					int count = resultQuery.getInt("count");
					int capacity = resultQuery.getInt("capacity");
					
					// here we have selected only free rooms, thus always true
					boolean available = true;
					FRPeriod period = new FRPeriod(start, end, false);
					FRRoom room = new FRRoom(doorCode, uid);

					//create the only actualoccupation for the room
					ActualOccupation accOcc = new ActualOccupation(period,
							available);
					accOcc.setProbableOccupation(count);
					accOcc.setRatioOccupation(capacity > 0 ? (double) count
							/ capacity : 0);

					//create the occupancy for the room
					Occupancy currentOccupancy = new Occupancy();
					currentOccupancy.setRoom(room);

					currentOccupancy.addToOccupancy(accOcc);
					currentOccupancy.setIsAtLeastFreeOnce(true);
					currentOccupancy.setIsAtLeastOccupiedOnce(false);
					// only one actualoccupancy per room in this mode
					currentOccupancy.setRatioWorstCaseProbableOccupancy(accOcc
							.getRatioOccupation());
					
					//extract building and insert it in the map
					String building = Utils.extractBuilding(doorCode);
					List<Occupancy> occOfBuilding = result.get(building);
					
					if (occOfBuilding == null) {
						occOfBuilding = new ArrayList<Occupancy>();
						result.put(building, occOfBuilding);
					}
					
					occOfBuilding.add(currentOccupancy);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * ResultSet given MUST contains the following fields : timestampStart,
	 * timestampEnd, capacity, count, uid, doorCode
	 * 
	 * AND BE SORTED by uid
	 * 
	 * @param resultQuery
	 * @param timestampStart
	 * @param timestampEnd
	 * @return
	 */

	private HashMap<String, List<Occupancy>> transformSQLResultInOccupancy(
			ResultSet resultQuery, long timestampStart, long timestampEnd) {

		boolean isAtLeastOccupiedOnce = false;
		boolean isAtLeastFreeOnce = false;

		HashMap<String, List<Occupancy>> occupancy = new HashMap<String, List<Occupancy>>();
		// timestamp used to generate the occupations accross the
		// FRPeriod

		ArrayList<Occupancy> allOccupancy = new ArrayList<Occupancy>();
		String currentUid = null;
		long tsPerRoom = timestampStart;
		Occupancy mCurrentOccupancy = new Occupancy();
		FRRoom currentRoom = null;
		try {
			while (resultQuery.next()) {
				String uid = resultQuery.getString("uid");

				if (currentUid != null && !currentUid.equals(uid)) {
					// we finished the previous uid, add the occupancy to the
					// list

					// There is some free time left after the last result
					if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
						ActualOccupation mOcc = new ActualOccupation();
						FRPeriod myPeriod = new FRPeriod(tsPerRoom,
								timestampEnd, false);
						mOcc.setPeriod(myPeriod);
						mOcc.setAvailable(true);
						mOcc.setProbableOccupation(0);
						mCurrentOccupancy.addToOccupancy(mOcc);
						isAtLeastFreeOnce = true;
					}

					// finalize this room
					allOccupancy.add(mCurrentOccupancy);
					mCurrentOccupancy = new Occupancy();
				}

				if (currentUid == null || !currentUid.equals(uid)) {
					// new room to handle
					currentUid = uid;
					tsPerRoom = timestampStart;
					String doorCode;
					doorCode = resultQuery.getString("doorCode");

					currentRoom = new FRRoom(doorCode, currentUid);
					currentRoom.setCapacity(resultQuery.getInt("capacity"));
					mCurrentOccupancy.setRoom(currentRoom);

					isAtLeastFreeOnce = false;
					isAtLeastOccupiedOnce = false;
				}

				long tsStart = Math.max(tsPerRoom,
						resultQuery.getLong("timestampStart"));
				long tsEnd = Math.min(timestampEnd,
						resultQuery.getLong("timestampEnd"));

				if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
					// We got a free period of time !
					ActualOccupation mOcc = new ActualOccupation();
					FRPeriod myPeriod = new FRPeriod(tsPerRoom, tsStart - 1,
							false);
					mOcc.setPeriod(myPeriod);
					mOcc.setAvailable(true);
					mOcc.setProbableOccupation(resultQuery.getInt("count"));
					mCurrentOccupancy.addToOccupancy(mOcc);
					isAtLeastFreeOnce = true;
				}

				ActualOccupation mAccOcc = new ActualOccupation();
				mAccOcc.setPeriod(new FRPeriod(tsStart, tsEnd, false));
				mAccOcc.setAvailable(false);
				mCurrentOccupancy.addToOccupancy(mAccOcc);
				isAtLeastOccupiedOnce = true;

				tsPerRoom = tsEnd;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<String, List<Occupancy>> getOccupancyOfSpecificRoom(
			List<String> uidList, boolean onlyFreeRooms) {
		return null;
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
					+ "SET count = count + 1 "
					+ "WHERE uid = (?) AND timestampStart = (?) AND timestampEnd = (?); \n";
			StringBuilder build = new StringBuilder(line.length() * size);
			for (int i = 0; i < size; i++) {
				build.append(line);
			}
			PreparedStatement query = connectBDD.prepareStatement(build
					.toString());

			// put the values in the query.
			for (int i = 0, j = 0; i < size; i++, j = 3 * i) {
				query.setString(j + 1, roomUID);
				FRPeriod period = mFrPeriods.get(i);
				query.setLong(j + 2, period.getTimeStampStart());
				query.setLong(j + 3, period.getTimeStampEnd());
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
	 * Returns all the rooms that satisfies the hint given in the request.
	 * 
	 * The hint may be the start of the door code or the uid.
	 * 
	 * TODO: verifies that it works with PH D2 398, PHD2 398, PH D2398 and
	 * PHD2398
	 * 
	 */
	@Override
	public AutoCompleteReply autoCompleteRoom(AutoCompleteRequest request)
			throws TException {
		AutoCompleteReply reply = new AutoCompleteReply(
				HttpURLConnection.HTTP_CREATED, ""
						+ HttpURLConnection.HTTP_CREATED);

		String constraint = request.getConstraint();

		if (constraint.length() < 2) {
			return new AutoCompleteReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"Constraints should be at least 2 characters long.");
		}

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
		// avoid all whitespaces for requests
		constraint = constraint.trim();
		constraint = constraint.replaceAll("\\s+", "");

		try {
			Connection connectBDD = connMgr.getConnection();
			String requestSQL = "";
			if (forbiddenRooms == null) {
				requestSQL = "SELECT * " + "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCode LIKE (?)) "
						+ "ORDER BY rl.doorCode ASC LIMIT 0, "
						+ LIMIT_AUTOCOMPLETE;
			} else {
				requestSQL = "SELECT * " + "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCode LIKE (?)) "
						+ "AND rl.uid NOT IN (" + forbidRoomsSQL + ") "
						+ "ORDER BY rl.doorCode ASC LIMIT 0, "
						+ LIMIT_AUTOCOMPLETE;
			}

			PreparedStatement query = connectBDD.prepareStatement(requestSQL);
			query.setString(1, constraint + "%");
			query.setString(2, constraint + "%");

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
			reply.setListRoom(Utils.sortRoomsByBuilding(rooms));

		} catch (SQLException e) {
			reply = new AutoCompleteReply(
					HttpURLConnection.HTTP_INTERNAL_ERROR, ""
							+ HttpURLConnection.HTTP_INTERNAL_ERROR);
			e.printStackTrace();
		}
		return reply;
	}

}
