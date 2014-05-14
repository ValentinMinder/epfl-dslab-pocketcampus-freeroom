package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.data.FetchOccupancyData;
import org.pocketcampus.plugin.freeroom.data.FetchOccupancyDataJSON;
import org.pocketcampus.plugin.freeroom.data.FetchRoomsDetails;
import org.pocketcampus.plugin.freeroom.server.exchange.ExchangeServiceImpl;
import org.pocketcampus.plugin.freeroom.server.utils.OccupancySorted;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.LogMessage;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
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

	private final int LIMIT_AUTOCOMPLETE = 50;
	private ConnectionManager connMgr;
	private ExchangeServiceImpl mExchangeService;
	private Logger logger = Logger.getLogger(FreeRoomServiceImpl.class
			.getName());
	private SimpleDateFormat dateLogFormat = new SimpleDateFormat(
			"MMM dd,yyyy HH:mm");

	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;

	// be careful when changing this, it might lead to invalid data already
	// stored !
	// this is what is used to differentiate a room from a student occupation in
	// the DB.
	public enum OCCUPANCY_TYPE {
		ROOM, USER;
	};

	// used to differentiate android log and server logs.
	private enum LOG_SIDE {
		ANDROID, SERVER;
	};

	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ... V2");
		logger.setLevel(Level.INFO);

		DB_URL = PC_SRV_CONFIG.getString("DB_URL") + "?allowMultiQueries=true";
		DB_USER = PC_SRV_CONFIG.getString("DB_USERNAME");
		DB_PASSWORD = PC_SRV_CONFIG.getString("DB_PASSWORD");

		try {
			connMgr = new ConnectionManager(DB_URL, DB_USER, DB_PASSWORD);
		} catch (ServerException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"Server cannot connect to the database");
			e.printStackTrace();
		}

		mExchangeService = new ExchangeServiceImpl(DB_URL, DB_USER,
				DB_PASSWORD, this);

		// update ewa : should be done periodically...
		boolean updateEWA = false;
		if (updateEWA) {
			if (mExchangeService.updateEWAOccupancy()) {
				System.out.println("EWA data succesfully updated!");
			} else {
				System.err.println("EWA data couldn't be completely loaded!");
			}
		}

		boolean updateRoomsDetails = false;
		if (updateRoomsDetails) {
			FetchRoomsDetails details = new FetchRoomsDetails(DB_URL, DB_USER,
					DB_PASSWORD);
			System.out.println(details.fetchRoomsIntoDB()
					+ " rooms inserted/updated");
		}

		// new FetchOccupancyData(PC_SRV_CONFIG.getString("DB_URL")
		// + "?allowMultiQueries=true",
		// PC_SRV_CONFIG.getString("DB_USERNAME"),
		// PC_SRV_CONFIG.getString("DB_PASSWORD"), this)
		// .fetchAndInsert(System.currentTimeMillis()
		// + FRTimes.ONE_DAY_IN_MS);

		FetchOccupancyDataJSON fodj = new FetchOccupancyDataJSON(DB_URL,
				DB_USER, DB_PASSWORD, this);

//		mCalendar.set(2014, 02, 18, 8, 0);
//		System.out.println(mCalendar);
//		long startSemester = mCalendar.getTimeInMillis();
//		mCalendar.set(2014, 06, 01, 8, 0);
//		long endSemester = mCalendar.getTimeInMillis();
//		fodj.fetchAndInsertRoomsList(startSemester, endSemester);
//		fodj.fetchAndInsert(System.currentTimeMillis());
	}

	public void log(Level level, String message) {
		log(LOG_SIDE.SERVER, level, message);
	}
	/**
	 * Logging function, time of the log will be set to the current timestamp.
	 * 
	 * @param type
	 *            Indicates from where the log comes from (i.e android, server
	 *            ...)
	 * @param level
	 *            Level of the bug (e.g Level.SEVERE, Level.WARNING ...)
	 * @param message
	 *            Content of the logging message
	 */
	private void log(LOG_SIDE type, Level level, String message) {
		log(type, level, message, System.currentTimeMillis());
	}

	/**
	 * Logging function.
	 * 
	 * @param type
	 *            Indicates from where the log comes from (i.e android, server
	 *            ...)
	 * @param level
	 *            Level of the bug (e.g Level.SEVERE, Level.WARNING ...)
	 * @param message
	 *            Content of the logging message
	 * @param timestamp
	 *            The time of the bug, might be different from the time when it
	 *            is called because log messages can come from various devices
	 *            (e.g android)
	 */
	private void log(LOG_SIDE type, Level level, String message, long timestamp) {
		logger.log(level,
				"[" + type.toString() + "] " + dateLogFormat.format(timestamp)
						+ " : " + message);
	}

	/**
	 * This method's job is to ensure the data are stored in a proper way.
	 * Whenever you need to insert an occupancy you should call this one. The
	 * start of a user occupancy should be a full hour (e.g 10h00). Timestamps
	 * may be modified before insertion in the following ways : seconds and
	 * milliseconds are set to 0, users occupancies are rounded to a full hour.
	 * 
	 * @param period
	 *            The period of the occupancy
	 * @param type
	 *            Type of the occupancy (for instance user or room occupancy)
	 * @param room
	 *            The room, the object has to contains the UID
	 * @return true if the occupancy has been well inserted, false otherwise.
	 */
	public boolean insertOccupancy(FRPeriod period, OCCUPANCY_TYPE type,
			String uid, String hash) {
		if (period == null || type == null || uid == null) {
			log(LOG_SIDE.SERVER,
					Level.WARNING,
					"Error during insertion of occupancy, at least one of the arguments is null : is null ? period = "
							+ (period == null)
							+ " type = "
							+ (type == null)
							+ " room = " + (uid == null));
			return false;
		}
		// putting seconds and milliseconds to zero
		period.setTimeStampStart(FRTimes.roundSAndMSToZero(period
				.getTimeStampStart()));
		period.setTimeStampEnd(FRTimes.roundSAndMSToZero(period
				.getTimeStampEnd()));

		if (type == OCCUPANCY_TYPE.USER && hash == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Hash is null when inserting user occupancy");
		}

		if (type == OCCUPANCY_TYPE.USER) {
			// round user occupancy to a full hour
			period.setTimeStampStart(FRTimes
					.roundToNearestHalfHourBefore(period.getTimeStampStart()));
		}

		boolean inserted = insertOccupancyAndCheckOccupancy(period, uid, type,
				hash);
		log(LOG_SIDE.SERVER, Level.INFO,
				"Inserting occupancy " + type.toString() + " for room " + uid
						+ " : " + inserted);
		return inserted;

	}

	/**
	 * This method checks whether the user has already submitted something for
	 * the same period, which is not allowed.
	 * 
	 * @param period
	 *            When the user submits its occupancy
	 * @param room
	 *            The room in which the user occupancy will be counted
	 * @param hash
	 *            The hash must be unique for each user and shouldn't depends on
	 *            time.
	 * @return true if the user occupancy is allowed and can be stored, false
	 *         otherwise.
	 */
	// TODO eventually do not user exact timestamp but allow margin even in
	// queries ?
	private String checkMultipleSubmissionUserOccupancy(long tsStart,
			String uid, String hash) {
		String checkRequest = "SELECT COUNT(*) AS count, co.uid "
				+ "FROM `fr-checkOccupancy` co "
				+ "WHERE co.timestampStart = ? AND hash = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement checkQuery = connectBDD
					.prepareStatement(checkRequest);

			checkQuery.setLong(1, tsStart);
			checkQuery.setString(2, hash);

			ResultSet checkResult = checkQuery.executeQuery();
			if (checkResult.next()) {
				int count = checkResult.getInt("count");
				if (count == 0) {
					return null;
				} else {
					return checkResult.getString("uid");
				}
			} else {
				return null;
			}

		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when checking multiple submissions of user occupancy start = "
							+ tsStart + " uid = " + uid);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Insert an occupancy in the database. It checks if there are no overlaps
	 * between rooms occupancies.
	 * 
	 * @param period
	 *            The period of the occupancy
	 * @param room
	 *            The room of the occupancy
	 * @param typeToInsert
	 *            Specify the type of occupancy (USER, ROOM)
	 * @param hash
	 *            The unique hash for each user, used to store an entry in the
	 *            checkOccupancy table to avoid multiple submissions for the
	 *            same period from an user
	 * @return Return true if the occupancy has been successfully stored in the
	 *         database, false otherwise.
	 */
	private boolean insertOccupancyAndCheckOccupancy(FRPeriod period,
			String uid, OCCUPANCY_TYPE typeToInsert, String hash) {

		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();
		boolean userOccupation = (typeToInsert == OCCUPANCY_TYPE.USER) ? true
				: false;

		// first check if you can fully insert it (no other overlapping
		// occupancy of rooms)
		String checkRequest = "SELECT * FROM `fr-occupancy` oc "
				+ "WHERE ((oc.timestampStart < ? AND oc.timestampStart > ?) "
				+ "OR (oc.timestampEnd > ? AND oc.timestampEnd < ?) "
				+ "OR (oc.timestampStart > ? AND oc.timestampEnd < ?)) AND oc.uid = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement checkQuery = connectBDD
					.prepareStatement(checkRequest);

			checkQuery.setLong(1, tsEnd);
			checkQuery.setLong(2, tsStart);
			checkQuery.setLong(3, tsStart);
			checkQuery.setLong(4, tsEnd);
			checkQuery.setLong(5, tsStart);
			checkQuery.setLong(6, tsEnd);
			checkQuery.setString(7, uid);

			ResultSet checkResult = checkQuery.executeQuery();

			while (checkResult.next()) {
				OCCUPANCY_TYPE type = OCCUPANCY_TYPE.valueOf(checkResult
						.getString("type"));

				// if we have a match and this is a room occupancy, we cannot go
				// further there is an overlap
				if (typeToInsert == OCCUPANCY_TYPE.ROOM
						&& type == OCCUPANCY_TYPE.ROOM) {
					log(LOG_SIDE.SERVER, Level.WARNING,
							"Error during insertion of occupancy, overlapping of two rooms occupancy, "
									+ "want to insert : " + uid
									+ " have conflict with " + uid);
					return false;
				}
			}
			// and now insert it !

			if (!userOccupation) {
				return insertOccupancyInDB(uid, tsStart, tsEnd,
						OCCUPANCY_TYPE.ROOM, 0);
			} else {

				boolean overallInsertion = true;

				long hourSharpBefore = FRTimes.roundHourBefore(tsStart);
				long numberHours = FRTimes.determineNumberHour(tsStart, tsEnd);

				for (int i = 0; i < numberHours; ++i) {
					// also insert in the check table to prevent further submit
					// during the same period from the same user
					String prevRoom = checkMultipleSubmissionUserOccupancy(
							hourSharpBefore + i * FRTimes.ONE_HOUR_IN_MS, uid,
							hash);
					if ((prevRoom != null && !prevRoom.equals(uid))
							|| prevRoom == null) {
						insertCheckOccupancyInDB(uid, hourSharpBefore + i
								* FRTimes.ONE_HOUR_IN_MS, hash, prevRoom);
						overallInsertion = overallInsertion
								&& insertOccupancyInDB(uid, hourSharpBefore + i
										* FRTimes.ONE_HOUR_IN_MS,
										hourSharpBefore + (i + 1)
												* FRTimes.ONE_HOUR_IN_MS,
										OCCUPANCY_TYPE.USER, 1);
					}

				}

				return overallInsertion;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when checking and inserting occupancies in DB for room = "
							+ uid + " start = " + period.getTimeStampStart()
							+ " end = " + period.getTimeStampEnd() + " hash = "
							+ hash + " type = " + typeToInsert.toString());
			return false;
		}
	}

	/**
	 * Insert an entry in the database which is used to deny multiple submits of
	 * user occupancies. It should not be called without pre-checking. If you
	 * want to insert a new occupancy call public insertOccupancy(...)
	 * 
	 * @param uid
	 *            The uid of the room
	 * @param tsStart
	 *            The start of the user occupancy
	 * @param hash
	 *            The unique hash per user
	 * @param prevRoom
	 *            The uid of the previous room beeing stored in the
	 *            checkOccupancy table (if one) null otherwise
	 */
	private void insertCheckOccupancyInDB(String uid, long tsStart,
			String hash, String prevRoom) {
		String insertRequest = "INSERT INTO `fr-checkOccupancy` (uid, timestampStart, hash) "
				+ "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE uid = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(insertRequest);

			insertQuery.setString(1, uid);
			insertQuery.setLong(2, tsStart);
			insertQuery.setString(3, hash);
			insertQuery.setString(4, uid);
			int update = insertQuery.executeUpdate();

			if (update > 1) {
				// we have updated the current row, thus we also need to adjust
				// the count value in fr-occupancy table
				decrementUserOccupancyCount(prevRoom, tsStart);
			}

		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when writing check Occupancy for uid = " + uid
							+ " hash = " + hash + " start = " + tsStart);
			e.printStackTrace();
		}

	}

	/**
	 * Decrement the count of users in the room by one
	 * 
	 * @param uid
	 *            The room to update
	 * @param tsStart
	 *            The period to update
	 */
	private void decrementUserOccupancyCount(String uid, long tsStart) {
		if (uid == null) {
			return;
		}

		String updateRequest = "UPDATE `fr-occupancy` co SET co.count = co.count - 1 "
				+ "WHERE co.uid = ? AND co.timestampStart = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(updateRequest);

			insertQuery.setString(1, uid);
			insertQuery.setLong(2, tsStart);
			int update = insertQuery.executeUpdate();

			System.out.println(update + " rooms updated (decrement)");

		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when updating (decrement by one) user occupancy for uid = "
							+ uid + " start = " + tsStart);
			e.printStackTrace();
		}
	}

	/**
	 * Insert a given occupancy in the database, if there is a duplicate key,
	 * the count field is incremented by one.
	 * 
	 * @param uid
	 *            The unique id of the room
	 * @param tsStart
	 *            The start of the period
	 * @param tsEnd
	 *            The end of the period
	 * @param type
	 *            The type of occupancy (USER, ROOM)
	 * @param count
	 *            The count associated to the occupancy
	 * @return If the query was successfull, false otherwise.
	 */
	private boolean insertOccupancyInDB(String uid, long tsStart, long tsEnd,
			OCCUPANCY_TYPE type, int count) {
		String insertRequest = "INSERT INTO `fr-occupancy` (uid, timestampStart, timestampEnd, type, count) "
				+ "VALUES (?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE count = count + 1";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(insertRequest);

			insertQuery.setString(1, uid);
			insertQuery.setLong(2, tsStart);
			insertQuery.setLong(3, tsEnd);
			insertQuery.setString(4, type.toString());
			insertQuery.setInt(5, count);

			insertQuery.execute();
			return true;
		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when inserting occupancy in DB, uid = " + uid
							+ " type = " + type.toString() + " start = "
							+ tsStart + " end = " + tsEnd);
			e.printStackTrace();
			return false;
		}
	}

	// for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
	}

	/**
	 * Get the occupancy of a given period, for a specific list of rooms of for
	 * any rooms. See thrift file for further informations about what can be
	 * requested.
	 */
	@Override
	public FRReply getOccupancy(FRRequest request) throws TException {
		if (request == null) {
			log(LOG_SIDE.SERVER, Level.WARNING, "Receiving null FRRequest");
			return new FRReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"FRRequest is null");
		}

		FRReply reply = checkFRRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		// round the given period to half hours to have a nice display on UI.
		FRPeriod period = request.getPeriod();
		period = FRTimes.roundFRRequestTimestamp(period);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		int group = request.getUserGroup();

		if (!FRTimes.validCalendars(period)) {
			// if something is wrong in the request
			return new FRReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"Bad timestamps! Your client sent a bad request, sorry");
		}

		boolean onlyFreeRoom = request.isOnlyFreeRooms();
		List<String> uidList = request.getUidList();

		HashMap<String, List<Occupancy>> occupancies = null;

		if (uidList == null || uidList.isEmpty()) {
			if (onlyFreeRoom) {
				// we want to look into all the rooms
				occupancies = getOccupancyOfAnyFreeRoom(onlyFreeRoom, tsStart,
						tsEnd, group);
			} else {
				return new FRReply(HttpURLConnection.HTTP_BAD_REQUEST,
						"The search for any free room must contains onlyFreeRoom = true");
			}
		} else {
			// or the user specified a specific list of rooms he wants to check
			occupancies = getOccupancyOfSpecificRoom(uidList, onlyFreeRoom,
					tsStart, tsEnd, group);
		}

		occupancies = sortRooms(occupancies);
		reply.setOccupancyOfRooms(occupancies);

		reply.setOverallTreatedPeriod(period);

		return reply;
	}

	private FRReply checkFRRequest(FRRequest request) {
		FRReply reply = new FRReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "FRRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "FRRequest is null;";
		} else {
			if (!request.isSetPeriod() || request.getPeriod() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "FRPeriod is null;";
			}

			if (!request.isSetOnlyFreeRooms()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "boolean onlyFreeRooms is not set;";
			}

			if (!request.isSetUserGroup()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "User group is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	/**
	 * The HashMap is organized by the following relation(building -> list of
	 * rooms) and each list of rooms is sorted independently. Sort the rooms
	 * according to some criterias. See the comparator roomsFreeComparator.
	 * 
	 * @param occ
	 *            The HashMap to be sorted
	 * @return The HashMap sorted
	 */
	private HashMap<String, List<Occupancy>> sortRooms(
			HashMap<String, List<Occupancy>> occ) {
		if (occ == null) {
			return null;
		}

		for (String key : occ.keySet()) {
			List<Occupancy> value = occ.get(key);
			Collections.sort(value, roomsFreeComparator);
		}

		return occ;
	}

	/**
	 * Comparator used to sort rooms according to some criterias. First put the
	 * rooms entirely free , then the partially occupied and then the rooms
	 * unavailable. Entirely free rooms are sorted by probable occupancy
	 * (users), partially occupied are sorted first by percentage of room
	 * occupation (i.e how many hours compared to the total period the room is
	 * occupied) then by probable occupancy (users).
	 */
	private Comparator<Occupancy> roomsFreeComparator = new Comparator<Occupancy>() {

		@Override
		public int compare(Occupancy o0, Occupancy o1) {

			boolean onlyFree1 = !o0.isIsAtLeastOccupiedOnce();
			boolean onlyFree2 = !o1.isIsAtLeastOccupiedOnce();
			boolean occupied1 = o0.isIsAtLeastOccupiedOnce();
			boolean occupied2 = o1.isIsAtLeastOccupiedOnce();
			boolean notFree1 = !onlyFree1 && occupied1;
			boolean notFree2 = !onlyFree2 && occupied2;

			if (onlyFree1 && onlyFree2) {
				return compareOnlyFree(o0.getRatioWorstCaseProbableOccupancy(),
						o1.getRatioWorstCaseProbableOccupancy());
			} else if (onlyFree1 && !onlyFree2) {
				return -1;
			} else if (!onlyFree1 && onlyFree2) {
				return 1;
			} else if (occupied1 && occupied2) {
				double rate1 = rateOccupied(o0.getOccupancy());
				double rate2 = rateOccupied(o1.getOccupancy());
				return comparePartiallyOccupied(rate1, rate2,
						o0.getRatioWorstCaseProbableOccupancy(),
						o1.getRatioWorstCaseProbableOccupancy());
			} else if (occupied1 && notFree2) {
				return -1;
			} else if (notFree1 && occupied2) {
				return 1;
			} else {
				return 0;
			}
		}

		private int comparePartiallyOccupied(double rate1, double rate2,
				double prob1, double prob2) {
			if (rate1 == rate2) {
				return equalPartiallyOccupied(prob1, prob2);
			} else if (rate1 < rate2) {
				return -1;
			} else {
				return 1;
			}
		}

		private int equalPartiallyOccupied(double prob1, double prob2) {
			if (prob1 < prob2) {
				return -1;
			} else if (prob1 > prob2) {
				return 1;
			}
			return 0;
		}

		/**
		 * Count the number of hours in the ActualOccupation given
		 * 
		 * @param acc
		 *            The ActualOccupation to be counted.
		 * @return The number of hours in the ActualOccupation
		 */
		private int countNumberHour(ActualOccupation acc) {
			long tsStart = acc.getPeriod().getTimeStampStart();
			long tsEnd = acc.getPeriod().getTimeStampEnd();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(tsStart);
			int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
			mCalendar.setTimeInMillis(tsEnd);
			int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);
			return Math.abs(endHour - startHour);
		}

		private double rateOccupied(List<ActualOccupation> occupations) {
			int count = 0;
			int total = 0;
			for (ActualOccupation acc : occupations) {
				int nbHours = countNumberHour(acc);
				if (!acc.isAvailable()) {
					count += nbHours;
				}
				total += nbHours;

			}
			return total > 0 ? (double) count / total : 0.0;
		}

		private int compareOnlyFree(double prob1, double prob2) {
			if (prob1 < prob2) {
				return -1;
			} else if (prob1 > prob2) {
				return +1;
			}
			return 0;
		}
	};

	/**
	 * Return the occupancy of all the free rooms during a given period.
	 * 
	 * @param onlyFreeRooms
	 *            Should always be true
	 * @param tsStart
	 *            The start of the period, should be rounded, see public
	 *            getOccupancy
	 * @param tsEnd
	 *            The end of the period, should be rounded, see public
	 *            getOccupancy
	 * @return A HashMap organized as follows (building -> list of free rooms in
	 *         the building)
	 */
	private HashMap<String, List<Occupancy>> getOccupancyOfAnyFreeRoom(
			boolean onlyFreeRooms, long tsStart, long tsEnd, int userGroup) {
		log(LOG_SIDE.SERVER, Level.INFO,
				"Requesting occupancy of any free rooms");
		HashMap<String, List<Occupancy>> result = new HashMap<String, List<Occupancy>>();
		if (onlyFreeRooms) {
			Connection connectBDD;
			try {
				connectBDD = connMgr.getConnection();
				// first select rooms totally free
				String request = "SELECT rl.uid, rl.doorCode, rl.capacity "
						+ "FROM `fr-roomslist` rl "
						+ "WHERE rl.uid NOT IN("
						+ "SELECT ro.uid FROM `fr-occupancy` ro "
						+ "WHERE ((ro.timestampEnd <= ? AND ro.timestampEnd >= ?) "
						+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
						+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?)) "
						+ "AND ro.type LIKE ?) AND rl.groupAccess <= ? AND rl.enabled = 1";

				PreparedStatement query = connectBDD.prepareStatement(request);
				query.setLong(1, tsEnd);
				query.setLong(2, tsStart);
				query.setLong(3, tsEnd);
				query.setLong(4, tsStart);
				query.setLong(5, tsStart);
				query.setLong(6, tsEnd);
				query.setString(7, OCCUPANCY_TYPE.ROOM.toString());
				query.setInt(8, userGroup);

				ResultSet resultQuery = query.executeQuery();

				ArrayList<String> uidsList = new ArrayList<String>();
				while (resultQuery.next()) {

					String uid = resultQuery.getString("uid");
					uidsList.add(uid);
				}

				if (uidsList.isEmpty()) {
					log(LOG_SIDE.SERVER, Level.WARNING,
							"No rooms are free during period start = "
									+ tsStart + " end = " + tsEnd);
					return new HashMap<String, List<Occupancy>>();
				}

				return getOccupancyOfSpecificRoom(uidsList, onlyFreeRooms,
						tsStart, tsEnd, userGroup);
			} catch (SQLException e) {
				e.printStackTrace();
				log(LOG_SIDE.SERVER, Level.SEVERE,
						"SQL error for occupancy of any free room, start = "
								+ tsStart + " end = " + tsEnd);
			}
		} else {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Getting request for any free rooms, with onlyFreeRoom attributes false");
		}

		return result;
	}

	/**
	 * Return the occupancies of the specified list of rooms.
	 * 
	 * @param uidList
	 *            The list of rooms to be checked
	 * @param onlyFreeRooms
	 *            If the results should contains only entirely free rooms or not
	 * @param tsStart
	 *            The start of the period, should be rounded, see public
	 *            getOccupancy
	 * @param tsEnd
	 *            The end of the period, should be rounded, see public
	 *            getOccupancy
	 * @return A HashMap organized as follows (building -> list of rooms in the
	 *         building)
	 */
	private HashMap<String, List<Occupancy>> getOccupancyOfSpecificRoom(
			List<String> uidList, boolean onlyFreeRooms, long tsStart,
			long tsEnd, int userGroup) {

		if (uidList.isEmpty()) {
			return getOccupancyOfAnyFreeRoom(onlyFreeRooms, tsStart, tsEnd,
					userGroup);
		}

		uidList = Utils.removeDuplicate(uidList);
		log(LOG_SIDE.SERVER, Level.INFO,
				"Requesting occupancy of specific list of rooms " + uidList);

		HashMap<String, List<Occupancy>> result = new HashMap<String, List<Occupancy>>();
		int numberOfRooms = uidList.size();
		// formatting for the query
		String roomsListQueryFormat = "";
		for (int i = 0; i < numberOfRooms - 1; ++i) {
			roomsListQueryFormat += "?,";
		}
		roomsListQueryFormat += "?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			String request = "SELECT rl.uid, rl.doorCode, rl.capacity, rl.alias, rl.typeEN, rl.typeFR, "
					+ "uo.count, uo.timestampStart, uo.timestampEnd, uo.type "
					+ "FROM `fr-roomslist` rl, `fr-occupancy` uo "
					+ "WHERE rl.uid = uo.uid AND rl.uid IN("
					+ roomsListQueryFormat
					+ ") "
					+ "AND ((uo.timestampEnd <= ? AND uo.timestampEnd >= ? ) "
					+ "OR (uo.timestampStart <= ? AND uo.timestampStart >= ?)"
					+ "OR (uo.timestampStart <= ? AND uo.timestampEnd >= ?)) "
					+ "ORDER BY rl.uid ASC, uo.timestampStart ASC";

			PreparedStatement query = connectBDD.prepareStatement(request);
			int i = 1;
			for (; i <= numberOfRooms; ++i) {
				query.setString(i, uidList.get(i - 1));
			}

			query.setLong(i, tsEnd);
			query.setLong(i + 1, tsStart);
			query.setLong(i + 2, tsEnd);
			query.setLong(i + 3, tsStart);
			query.setLong(i + 4, tsStart);
			query.setLong(i + 5, tsEnd);

			ResultSet resultQuery = query.executeQuery();

			String currentUID = null;
			String currentDoorCode = null;
			OccupancySorted currentOccupancy = null;

			// We can add the ActualOccupation as they come, no worries about
			// the order, the class OccupancySorted sorts and deals with it
			while (resultQuery.next()) {
				// extract attributes of record
				long start = resultQuery.getLong("timestampStart");
				long end = resultQuery.getLong("timestampEnd");
				String uid = resultQuery.getString("uid");
				int count = resultQuery.getInt("count");
				String doorCode = resultQuery.getString("doorCode");
				String alias = resultQuery.getString("alias");
				String typeFR = resultQuery.getString("typeFR");
				String typeEN = resultQuery.getString("typeEN");

				OCCUPANCY_TYPE type = OCCUPANCY_TYPE.valueOf(resultQuery
						.getString("type"));
				boolean available = (type == OCCUPANCY_TYPE.USER) ? true
						: false;
				int capacity = resultQuery.getInt("capacity");
				double ratio = capacity > 0 ? (double) count / capacity : 0.0;

				FRPeriod period = new FRPeriod(start, end, false);
				FRRoom mRoom = new FRRoom(doorCode, uid);
				mRoom.setBuilding_name(Utils.extractBuilding(doorCode));

				mRoom.setCapacity(capacity);
				if (alias != null) {
					mRoom.setDoorCodeAlias(alias);
				}
				if (typeEN != null) {
					mRoom.setTypeEN(typeEN);
				}
				if (typeFR != null) {
					mRoom.setTypeFR(typeFR);
				}

				// if this is the first iteration
				if (currentUID == null) {
					currentUID = uid;
					currentDoorCode = mRoom.getDoorCode();
					currentOccupancy = new OccupancySorted(mRoom, tsStart,
							tsEnd, onlyFreeRooms);
				}

				// we move on to the next room thus re-initialize attributes
				// for the loop, as well as storing the previous room in the
				// resulting HashMap
				if (!uid.equals(currentUID)) {
					Occupancy mOccupancy = currentOccupancy.getOccupancy();

					addToHashMapOccupancy(currentDoorCode, mOccupancy, result);

					// remove the room from the list, this is important as all
					// the rooms might not be matched by the query (if there are
					// no entry for instance)
					uidList.remove(currentUID);

					// re-initialize the value, and continue the process for
					// other rooms
					currentDoorCode = mRoom.getDoorCode();
					currentOccupancy = new OccupancySorted(mRoom, tsStart,
							tsEnd, onlyFreeRooms);
					currentUID = uid;
				}

				ActualOccupation accOcc = new ActualOccupation(period,
						available);
				accOcc.setProbableOccupation(count);
				accOcc.setRatioOccupation(ratio);
				currentOccupancy.addActualOccupation(accOcc);
			}

			// the last room has not been added yet
			if (currentOccupancy != null && currentOccupancy.size() != 0) {
				Occupancy mOccupancy = currentOccupancy.getOccupancy();

				addToHashMapOccupancy(currentDoorCode, mOccupancy, result);

				// remove the room from the list
				uidList.remove(currentUID);
			}

			// for all the others rooms that hasn't been matched in the query,
			// we need to add them too
			if (!uidList.isEmpty()) {
				roomsListQueryFormat = "";
				for (i = 0; i < uidList.size() - 1; ++i) {
					roomsListQueryFormat += "?,";
				}

				roomsListQueryFormat += "?";
				String infoRequest = "SELECT rl.uid, rl.doorCode, rl.capacity, rl.alias, rl.typeEN, rl.typeFR "
						+ "FROM `fr-roomslist` rl "
						+ "WHERE rl.uid IN("
						+ roomsListQueryFormat + ")";

				PreparedStatement infoQuery = connectBDD
						.prepareStatement(infoRequest);

				for (i = 1; i <= uidList.size(); ++i) {
					infoQuery.setString(i, uidList.get(i - 1));
				}

				ResultSet infoRoom = infoQuery.executeQuery();

				while (infoRoom.next()) {
					String uid = infoRoom.getString("uid");
					String doorCode = infoRoom.getString("doorCode");
					int capacity = infoRoom.getInt("capacity");
					String alias = infoRoom.getString("alias");
					String typeFR = infoRoom.getString("typeFR");
					String typeEN = infoRoom.getString("typeEN");

					FRRoom mRoom = new FRRoom(doorCode, uid);
					mRoom.setCapacity(capacity);
					if (alias != null) {
						mRoom.setDoorCodeAlias(alias);
					}
					if (typeEN != null) {
						mRoom.setTypeEN(typeEN);
					}
					if (typeFR != null) {
						mRoom.setTypeFR(typeFR);
					}

					currentOccupancy = new OccupancySorted(mRoom, tsStart,
							tsEnd, onlyFreeRooms);
					FRPeriod period = new FRPeriod(tsStart, tsEnd, false);
					ActualOccupation accOcc = new ActualOccupation(period, true);
					accOcc.setProbableOccupation(0);
					currentOccupancy.addActualOccupation(accOcc);

					Occupancy mOccupancy = currentOccupancy.getOccupancy();
					addToHashMapOccupancy(mRoom.getDoorCode(), mOccupancy,
							result);
				}
			}
		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error of occupancy of specific list of rooms "
							+ uidList + " start = " + tsStart + " end = "
							+ tsEnd);
		}
		return result;
	}

	/**
	 * Add to a given HashMap the given occupancy by extracting the building
	 * from the doorCode. The HashMap maps a building to a list of Occupancy for
	 * rooms in its building.
	 * 
	 * @param doorCode
	 *            The door code of the room to add
	 * @param mOcc
	 *            The Occupancy of the room
	 * @param result
	 *            The HashMap in which we add the room
	 */
	private void addToHashMapOccupancy(String doorCode, Occupancy mOcc,
			HashMap<String, List<Occupancy>> result) {
		if (mOcc == null || doorCode == null) {
			return;
		}
		String building = Utils.extractBuilding(doorCode);
		List<Occupancy> occ = result.get(building);

		if (occ == null) {
			occ = new ArrayList<Occupancy>();
			result.put(building, occ);
		}
		occ.add(mOcc);
	}

	/**
	 * Returns all the rooms that satisfies the hint given in the request.
	 * 
	 * The hint may be the start of the door code or the uid or even the alias.
	 * 
	 * Constraints should be at least 2 characters long. You can specify a list
	 * of forbidden rooms the server should not include in the response. The
	 * number of results is bounded by the constant LIMIT_AUTOCOMPLETE.
	 * 
	 */
	@Override
	public AutoCompleteReply autoCompleteRoom(AutoCompleteRequest request)
			throws TException {
		if (request == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Receiving null AutoCompleteRequest");
			return new AutoCompleteReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"AutocompleteRequest is null");
		}

		AutoCompleteReply reply = checkAutoCompleteRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		log(LOG_SIDE.SERVER, Level.INFO,
				"Autocomplete of " + request.getConstraint());

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
		constraint = constraint.replaceAll("\\s+", "");

		try {
			Connection connectBDD = connMgr.getConnection();
			String requestSQL = "";
			if (forbiddenRooms == null) {
				requestSQL = "SELECT * "
						+ "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCodeWithoutSpace LIKE (?) OR rl.alias LIKE (?)) "
						+ "AND rl.groupAccess <= ? AND rl.enabled = 1 "
						+ "ORDER BY rl.doorCode ASC LIMIT "
						+ LIMIT_AUTOCOMPLETE;
			} else {
				requestSQL = "SELECT * "
						+ "FROM `fr-roomslist` rl "
						+ "WHERE (rl.uid LIKE (?) OR rl.doorCodeWithoutSpace LIKE (?) OR rl.alias LIKE (?)) "
						+ "AND rl.groupAccess <= ? AND rl.enabled = 1 AND rl.uid NOT IN ("
						+ forbidRoomsSQL + ") "
						+ "ORDER BY rl.doorCode ASC LIMIT "
						+ LIMIT_AUTOCOMPLETE;
			}

			PreparedStatement query = connectBDD.prepareStatement(requestSQL);
			query.setString(1, constraint + "%");
			query.setString(2, constraint + "%");
			query.setString(3, constraint + "%");
			query.setInt(4, request.getUserGroup());

			if (forbiddenRooms != null) {
				int i = 5;
				for (String roomUID : forbiddenRooms) {
					query.setString(i, roomUID);
					++i;
				}
			}

			// filling the query with values

			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String doorCode = resultQuery.getString("doorCode");
				FRRoom frRoom = new FRRoom(doorCode,
						resultQuery.getString("uid"));
				frRoom.setBuilding_name(Utils.extractBuilding(doorCode));

				int cap = resultQuery.getInt("capacity");
				if (cap > 0) {
					frRoom.setCapacity(cap);
				}
				String alias = resultQuery.getString("alias");
				if (alias != null) {
					frRoom.setDoorCodeAlias(alias);
				}

				String typeFR = resultQuery.getString("typeFR");
				if (typeFR != null) {
					frRoom.setTypeFR(typeFR);
				}

				String typeEN = resultQuery.getString("typeEN");
				if (typeEN != null) {
					frRoom.setTypeEN(typeEN);
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
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error for autocomplete request with constraint "
							+ constraint);
		}
		return reply;
	}

	private AutoCompleteReply checkAutoCompleteRequest(
			AutoCompleteRequest request) {
		AutoCompleteReply reply = new AutoCompleteReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "AutoCompleteRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "AutoCompleteRequest is null;";
		} else {
			if (!request.isSetConstraint() || request.getConstraint() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Constraint is null;";
			}

			if (!request.isSetUserGroup()) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "User group is not set;";
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	/**
	 * The client can specify a user occupancy during a given period, multiple
	 * submits for the same period (and same user) are not allowed, we return a
	 * HTTP_CONFLICT in that case.
	 */
	@Override
	public ImWorkingReply indicateImWorking(ImWorkingRequest request)
			throws TException {
		if (request == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Receiving null ImWorkingRequest");
			return new ImWorkingReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"ImWorkingReply is null");
		}

		ImWorkingReply reply = checkImWorkingRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		WorkingOccupancy work = request.getWork();
		FRPeriod period = work.getPeriod();

		FRRoom room = work.getRoom();
		boolean success = insertOccupancy(period, OCCUPANCY_TYPE.USER,
				room.getUid(), request.getHash());
		log(LOG_SIDE.SERVER, Level.INFO, "ImWorkingThere request for room "
				+ room.getDoorCode() + " : " + success);
		if (success) {
			return new ImWorkingReply(HttpURLConnection.HTTP_OK, "");
		} else {
			return new ImWorkingReply(HttpURLConnection.HTTP_CONFLICT,
					"User already said he was working there");
		}
	}

	private ImWorkingReply checkImWorkingRequest(ImWorkingRequest request) {
		ImWorkingReply reply = new ImWorkingReply();
		int status = HttpURLConnection.HTTP_OK;
		String statusComment = "ImWorkingRequest : ";

		if (request == null) {
			status = HttpURLConnection.HTTP_BAD_REQUEST;
			statusComment = "ImWorkingRequest is null;";
		} else {
			if (!request.isSetWork() || request.getWork() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "work (WorkingOccupancy) is null;";
			}

			if (!request.isSetHash() || request.getHash() == null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += "Hash is not set;";
			}

			String workCheck = checkWorkingOccupancy(request.getWork());
			if (workCheck != null) {
				status = HttpURLConnection.HTTP_BAD_REQUEST;
				statusComment += workCheck;
			}
		}

		reply.setStatus(status);
		if (status != HttpURLConnection.HTTP_OK) {
			reply.setStatusComment(statusComment);
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}
		return reply;
	}

	private String checkWorkingOccupancy(WorkingOccupancy work) {
		boolean error = false;
		String comment = "WorkingOccupancy ";
		if (work == null) {
			comment += "is null;";
			error = true;
		}

		if (!work.isSetPeriod() || work.getPeriod() == null) {
			comment += "FRPeriod is not set;";
		}

		if (!work.isSetRoom() || work.getRoom() == null) {
			comment += "FRRoom is not set;";
		}

		if (error) {
			return comment;
		}
		return null;
	}

	@Override
	public WhoIsWorkingReply whoIsWorking(WhoIsWorkingRequest request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Pre-format the message for logging
	 * 
	 * @param message
	 *            The message
	 * @param path
	 *            The path to the file where the bug happened
	 * @return A pre-formatted message containing the path and the message.
	 */
	private String formatPathMessageLogAndroid(String message, String path) {
		return path + " / " + message;
	}

	/**
	 * Log Severe messages coming from external clients such as android.
	 */
	@Override
	public void logSevere(LogMessage arg0) throws TException {
		log(LOG_SIDE.ANDROID, Level.SEVERE,
				formatPathMessageLogAndroid(arg0.getMessage(), arg0.getPath()),
				arg0.getTimestamp());
	}

	/**
	 * Log Warning messages coming from external clients such as android.
	 */
	@Override
	public void logWarning(LogMessage arg0) throws TException {
		log(LOG_SIDE.ANDROID, Level.WARNING,
				formatPathMessageLogAndroid(arg0.getMessage(), arg0.getPath()),
				arg0.getTimestamp());
	}

}
