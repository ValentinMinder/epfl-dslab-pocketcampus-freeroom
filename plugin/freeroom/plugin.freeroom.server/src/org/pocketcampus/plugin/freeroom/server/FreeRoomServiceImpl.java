package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.utils.CheckRequests;
import org.pocketcampus.plugin.freeroom.server.utils.OccupancySorted;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteUserMessageReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteUserMessageRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.LogMessage;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.RegisterUser;
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
	private final int LENGTH_USERMESSAGE = 30;

	private ConnectionManager connMgr;
	private Logger logger = Logger.getLogger(FreeRoomServiceImpl.class
			.getName());
	private SimpleDateFormat dateLogFormat = new SimpleDateFormat(
			"MMM dd,yyyy HH:mm");
	private final String LOG_FOLDER = "log";
	private final String PATH_LOG_PATTERN = "./" + LOG_FOLDER
			+ "/freeroom%g.log";
	// total size of log can be MAX_BYTES_PER_LOGFILE * MAX_LOGFILES
	private final int MAX_BYTES_PER_LOGFILE = 4 * 1000 * 1000;
	private final int MAX_LOGFILES = 200;
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
		createLogFolder();
		logger.setLevel(Level.INFO);
		FileHandler logHandler = null;
		try {
			logHandler = new FileHandler(PATH_LOG_PATTERN,
					MAX_BYTES_PER_LOGFILE, MAX_LOGFILES, true);
			SimpleFormatter logFormatter = new SimpleFormatter();
			logHandler.setFormatter(logFormatter);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (logHandler != null) {
			logger.addHandler(logHandler);

		}

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
		// new Thread(new PeriodicallyUpdate(DB_URL, DB_USER, DB_PASSWORD,
		// this)).start();
	}

	// for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
	}

	private void createLogFolder() {
		File folder = new File("./" + LOG_FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public void log(Level level, String message) {
		log(LOG_SIDE.SERVER, level, message);
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

	private String formatServerLogInfo(String method, String arguments) {
		return formatServerLogInfo(method, arguments, "NA");

	}

	private String formatServerLogInfo(String method, String arguments,
			String answer) {
		return "ACTION=" + method + " / ARGS=" + arguments + " / RETURN="
				+ answer;
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

	public boolean insertOccupancy(FRPeriod period, OCCUPANCY_TYPE type,
			String uid, String hash, String userMessage) {
		return insertOccupancyDetailedReply(period, type, uid, hash,
				userMessage) == HttpURLConnection.HTTP_OK;
	}

	/**
	 * This method's job is to ensure the data are stored in a proper way.
	 * Whenever you need to insert an occupancy you should call this one. The
	 * start of a user occupancy should be a full hour (e.g 10h00). Timestamps
	 * may be modified before insertion in the following ways : seconds and
	 * milliseconds are set to 0, users occupancies are rounded to a half hour
	 * before.
	 * 
	 * @param period
	 *            The period of the occupancy
	 * @param type
	 *            Type of the occupancy (for instance user or room occupancy)
	 * @param room
	 *            The room, the object has to contains the UID
	 * @return int error code defined by HttpURLConnection, OK insertion is
	 *         successful, BAD_REQUEST, argument required is (are) null, or
	 *         length of the message is too long, PRECON_FAILED if the message
	 *         of the user contains forbidden words, INTERNAL_ERROR if the
	 *         server failed
	 */
	public int insertOccupancyDetailedReply(FRPeriod period,
			OCCUPANCY_TYPE type, String uid, String hash, String userMessage) {
		if (period == null || type == null || uid == null) {
			log(LOG_SIDE.SERVER,
					Level.WARNING,
					"Error during insertion of occupancy, at least one of the arguments is null : is null ? period = "
							+ (period == null)
							+ " type = "
							+ (type == null)
							+ " room = " + (uid == null));
			return HttpURLConnection.HTTP_BAD_REQUEST;
		}
		// putting seconds and milliseconds to zero
		period.setTimeStampStart(FRTimes.roundSAndMSToZero(period
				.getTimeStampStart()));
		period.setTimeStampEnd(FRTimes.roundSAndMSToZero(period
				.getTimeStampEnd()));

		if (type == OCCUPANCY_TYPE.USER && hash == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Hash is null when inserting user occupancy");
			return HttpURLConnection.HTTP_BAD_REQUEST;
		}

		if (type == OCCUPANCY_TYPE.USER) {
			// round user occupancy to a full hour
			// period.setTimeStampStart(FRTimes
			// .roundToNearestHalfHourBefore(period.getTimeStampStart()));
			period.setTimeStampStart(FRTimes.roundHourBefore(period
					.getTimeStampStart()));
			if (!Utils.checkUserMessage(userMessage)) {
				log(Level.WARNING, "Getting wrong user message : "
						+ userMessage);
				return HttpURLConnection.HTTP_PRECON_FAILED;
			} else if (userMessage != null
					&& userMessage.length() > LENGTH_USERMESSAGE) {
				log(Level.INFO, "User message is too long, length = "
						+ userMessage.length());
				return HttpURLConnection.HTTP_BAD_REQUEST;
			}
		}

		boolean inserted = insertOccupancyAndCheckOccupancyInDB(period, uid,
				type, hash, userMessage);
		log(LOG_SIDE.SERVER,
				Level.INFO,
				formatServerLogInfo(
						"insertOccupancy",
						"type=" + type.toString() + ",uid=" + uid + ",start="
								+ period.getTimeStampStart() + ",end="
								+ period.getTimeStampEnd() + ",userMessage="
								+ userMessage, inserted + ""));
		return inserted ? HttpURLConnection.HTTP_OK
				: HttpURLConnection.HTTP_INTERNAL_ERROR;

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
	private boolean insertOccupancyAndCheckOccupancyInDB(FRPeriod period,
			String uid, OCCUPANCY_TYPE typeToInsert, String hash,
			String userMessage) {

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
					String prevRoom = getLastUID(hourSharpBefore + i
							* FRTimes.ONE_HOUR_IN_MS, uid, hash);
					String prevMessage = getLastMessage(hourSharpBefore + i
							* FRTimes.ONE_HOUR_IN_MS, uid, hash);

					// if first insertion or update of room
					insertOrUpdateCheckOccupancy(uid, hourSharpBefore + i
							* FRTimes.ONE_HOUR_IN_MS, hash, prevRoom,
							prevMessage, userMessage);
					if (prevRoom == null
							|| (prevRoom != null && !prevRoom.equals(uid))) {
						// we increment the counter only if this is the first
						// insertion (prevRoom == null) or if we change the
						// room, in that case the counter for the new room has
						// to be incremented, the old one has been taken care of
						// in the previous method
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
	 * @return The previous UID or null if none
	 */
	// TODO eventually do not user exact timestamp but allow margin even in
	// queries ?
	private String getLastUID(long tsStart, String uid, String hash) {
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

	private String getLastMessage(long tsStart, String uid, String hash) {
		String checkRequest = "SELECT COUNT(*) AS count, co.message "
				+ "FROM `fr-checkOccupancy` co "
				+ "WHERE co.timestampStart = ? AND co.hash = ? AND co.message IS NOT NULL ";

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
					return checkResult.getString("message");
				}
			} else {
				return null;
			}

		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when getting message of user occupancy start = "
							+ tsStart + " uid = " + uid + " hash = " + hash);
			e.printStackTrace();
			return null;
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
	// TODO test if usermessage change, there is no problem with count
	private void insertOrUpdateCheckOccupancy(String uid, long tsStart,
			String hash, String prevRoom, String prevMessage, String userMessage) {

		if (prevRoom == null) {
			// first insertion, no problem with duplicate key
			insertCheckOccupancyInDB(uid, tsStart, hash, userMessage);
		} else {
			String duplicateMessage = null;

			if (userMessage == null) {
				// no new message, take the old one
				duplicateMessage = prevMessage;
			} else {
				// new message, use the new one
				duplicateMessage = userMessage;
			}

			if (duplicateMessage == null) {
				duplicateMessage = "";
			}
			updateCheckOccupancyInDB(uid, prevRoom, tsStart, hash,
					duplicateMessage, !prevRoom.equals(uid));
		}

	}

	// TODO javaodc with new methods def.
	private void insertCheckOccupancyInDB(String uid, long tsStart,
			String hash, String message) {
		if (message == null) {
			message = "";
		}
		String insertRequest = "INSERT INTO `fr-checkOccupancy` (uid, timestampStart, timestampEnd, hash, message) "
				+ "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uid = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(insertRequest);

			insertQuery.setString(1, uid);
			insertQuery.setLong(2, tsStart);
			insertQuery.setLong(3, tsStart + FRTimes.ONE_HOUR_IN_MS);
			insertQuery.setString(4, hash);
			if (message != null) {
				insertQuery.setString(5, message);
			} else {
				insertQuery.setNull(5, Types.CHAR);
			}
			insertQuery.setString(6, uid);
			int update = insertQuery.executeUpdate();

			if (update > 1) {
				// should not happen
				log(Level.SEVERE,
						"Inserting occupancy but duplicate key, and duplicate key shouldn t be there for uid = "
								+ uid
								+ " hash = "
								+ hash
								+ " tsStar = "
								+ tsStart);
			}

		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when writing check Occupancy for uid = " + uid
							+ " hash = " + hash + " start = " + tsStart);
			e.printStackTrace();
		}
	}

	/**
	 * Update checkOccupancy attributs
	 * 
	 * @param uid
	 *            The new uid
	 * @param prevRoom
	 *            The previous uid to be updated
	 * @param tsStart
	 *            The timestamp of the record to update
	 * @param hash
	 *            The hash of the record to update
	 * @param message
	 *            The new message
	 * @param updateCount
	 *            If we should also decrement the count in occupancy table for
	 *            the old uid
	 */
	private void updateCheckOccupancyInDB(String uid, String prevRoom,
			long tsStart, String hash, String message, boolean updateCount) {

		String insertRequest = "UPDATE `fr-checkOccupancy` SET uid = ?, message = ? "
				+ "WHERE uid = ? AND timestampStart = ? AND hash = ?";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(insertRequest);

			insertQuery.setString(1, uid);
			insertQuery.setString(2, message);
			insertQuery.setString(3, prevRoom);
			insertQuery.setLong(4, tsStart);
			insertQuery.setString(5, hash);

			insertQuery.executeUpdate();

			if (updateCount) {
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
	 * @return true if successfully decremented
	 */
	private boolean decrementUserOccupancyCount(String uid, long tsStart) {
		if (uid == null) {
			return false;
		}

		String updateRequest = "UPDATE `fr-occupancy` co SET co.count = co.count - 1 "
				+ "WHERE co.uid = ? AND co.timestampStart = ? AND count >= 1";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement insertQuery = connectBDD
					.prepareStatement(updateRequest);

			insertQuery.setString(1, uid);
			insertQuery.setLong(2, tsStart);
			int update = insertQuery.executeUpdate();

			if (update == 0) {
				log(Level.WARNING,
						"Cannot decrement count of user occupancy for uid = "
								+ uid + " tsStart = " + tsStart);
				return false;
			}
			return true;
		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when updating (decrement by one) user occupancy for uid = "
							+ uid + " start = " + tsStart);
			e.printStackTrace();
			return false;
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

		FRReply reply = CheckRequests.checkFRRequest(request);
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

		if (occupancies == null) {
			return new FRReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					HttpURLConnection.HTTP_INTERNAL_ERROR + "");
		}

		occupancies = Utils.sortRooms(occupancies);
		reply.setOccupancyOfRooms(occupancies);

		reply.setOverallTreatedPeriod(period);

		return reply;
	}

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
	 *         the building), null if an error occured
	 */
	private HashMap<String, List<Occupancy>> getOccupancyOfAnyFreeRoom(
			boolean onlyFreeRooms, long tsStart, long tsEnd, int userGroup) {

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

				String logMessage = "onlyFreeRooms=" + onlyFreeRooms
						+ ",tsStart=" + tsStart + ",tsEnd=" + tsEnd
						+ ",userGroup=" + userGroup;
				log(Level.INFO,
						formatServerLogInfo("getOccupancyOfAnyFreeRoom",
								logMessage));
				return getOccupancyOfSpecificRoom(uidsList, onlyFreeRooms,
						tsStart, tsEnd, userGroup);
			} catch (SQLException e) {
				e.printStackTrace();
				log(LOG_SIDE.SERVER, Level.SEVERE,
						"SQL error for occupancy of any free room, start = "
								+ tsStart + " end = " + tsEnd);
				return null;
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
	 *         building), null if an error occured
	 */
	private HashMap<String, List<Occupancy>> getOccupancyOfSpecificRoom(
			List<String> uidList, boolean onlyFreeRooms, long tsStart,
			long tsEnd, int userGroup) {

		if (uidList.isEmpty()) {
			return getOccupancyOfAnyFreeRoom(onlyFreeRooms, tsStart, tsEnd,
					userGroup);
		}

		uidList = Utils.removeDuplicate(uidList);

		String logMessage = "uidList=" + uidList + ",onlyFreeRooms="
				+ onlyFreeRooms + ",tsStart=" + tsStart + ",tsEnd=" + tsEnd
				+ ",userGroup=" + userGroup;

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

			log(Level.INFO,
					formatServerLogInfo("getOccupancyOfSpecificRoom",
							logMessage));
		} catch (SQLException e) {
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error of occupancy of specific list of rooms "
							+ uidList + " start = " + tsStart + " end = "
							+ tsEnd);
			return null;
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

		AutoCompleteReply reply = CheckRequests
				.checkAutoCompleteRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

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

			String logMessage = "constraint=" + constraint + ",forbiddenRooms="
					+ forbiddenRooms;
			log(Level.INFO, formatServerLogInfo("autoCompleteRoom", logMessage));
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

	@Override
	public AutoCompleteUserMessageReply autoCompleteUserMessage(
			AutoCompleteUserMessageRequest request) throws TException {
		if (request == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Receiving null AutoCompleteUserMessageRequest");
			return new AutoCompleteUserMessageReply(
					HttpURLConnection.HTTP_BAD_REQUEST,
					"AutocompleteUserMessageRequest is null");
		}

		AutoCompleteUserMessageReply reply = CheckRequests
				.checkAutoCompleteUserMessageRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		String constraint = request.getConstraint().replaceAll("\\s+", "");
		String uid = request.getRoom().getUid();
		FRPeriod period = request.getPeriod();
		// TODO change getUserMessage with FRRoom, not only uid to be more
		// consistent
		// TODO check valid period
		String requestSQL = "SELECT co.message "
				+ "FROM `fr-checkOccupancy` co "
				+ "WHERE co.uid = ? AND co.timestampStart >= ? AND co.timestampEnd <= ? "
				+ "AND LOWER(co.message) LIKE (?) ORDER BY co.message ASC";

		try {
			ArrayList<String> messages = new ArrayList<String>();

			Connection connectBDD = connMgr.getConnection();

			PreparedStatement query = connectBDD.prepareStatement(requestSQL);
			query.setString(1, uid);
			query.setLong(2, period.getTimeStampStart());
			query.setLong(3, period.getTimeStampEnd());
			query.setString(4, "%" + constraint.toLowerCase() + "%");

			ResultSet result = query.executeQuery();

			while (result.next()) {
				messages.add(result.getString("message"));
			}

			String logMessage = "constraint=" + constraint;
			log(Level.INFO,
					formatServerLogInfo("autoCompleteUserMessage", logMessage));
			reply.setMessages(messages);
		} catch (SQLException e) {
			;
			e.printStackTrace();
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when autocompleting user message for uid = "
							+ uid + " period = " + period + " constraint = "
							+ constraint);
			return new AutoCompleteUserMessageReply(
					HttpURLConnection.HTTP_INTERNAL_ERROR,
					"Error when autocompleting");
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

		ImWorkingReply reply = CheckRequests.checkImWorkingRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		WorkingOccupancy work = request.getWork();
		FRPeriod period = work.getPeriod();
		String userMessage = (work.isSetMessage() && work.getMessage() != null) ? work
				.getMessage() : null;
		FRRoom room = work.getRoom();
		int code = insertOccupancyDetailedReply(period, OCCUPANCY_TYPE.USER,
				room.getUid(), request.getHash(), userMessage);
		// TODO maybe change return value, to match more cases
		if (code == HttpURLConnection.HTTP_OK) {
			String logMessage = "start=" + period.getTimeStampStart() + ",end="
					+ period.getTimeStampEnd() + ",uid= " + room.getUid()
					+ ",hash=" + request.getHash() + ",userMessage="
					+ userMessage;
			log(Level.INFO,
					formatServerLogInfo("indicateImWorking", logMessage));
		}
		return new ImWorkingReply(code, " ");

	}

	@Override
	public WhoIsWorkingReply getUserMessages(WhoIsWorkingRequest request)
			throws TException {
		if (request == null) {
			log(LOG_SIDE.SERVER, Level.WARNING,
					"Receiving null WhoIsWorkingRequest");
			return new WhoIsWorkingReply(HttpURLConnection.HTTP_BAD_REQUEST,
					"WhoIsWorkingRequest is null");
		}

		WhoIsWorkingReply reply = CheckRequests
				.checkWhoIsWorkingRequest(request);
		if (reply.getStatus() != HttpURLConnection.HTTP_OK) {
			log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
			return reply;
		} else {
			reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
		}

		FRPeriod period = request.getPeriod();

		// TODO remove duplicate
		// period.setTimeStampStart(FRTimes
		// .roundToNearestHalfHourBefore(period.getTimeStampStart()));

		List<String> listMessages = getUserMessages(period,
				request.getRoomUID());
		if (listMessages == null) {
			return new WhoIsWorkingReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					HttpURLConnection.HTTP_INTERNAL_ERROR + "");
		} else {
			reply.setMessages(Utils.removeGroupMessages(listMessages));
			String logMessage = "uid=" + request.getRoomUID() + ",start="
					+ period.getTimeStampStart() + ",end="
					+ period.getTimeStampEnd();
			log(Level.INFO, formatServerLogInfo("getUserMessages", logMessage));
		}
		return reply;
	}

	/**
	 * Get the list of messages for a given period and a given room
	 * 
	 * @param period
	 *            The period of the search
	 * @param uid
	 *            The room of the search
	 * @return A list of messages with the messages or empty list if none, null
	 *         if an error occured
	 */
	private List<String> getUserMessages(FRPeriod period, String uid) {
		try {
			ArrayList<String> messages = new ArrayList<String>();
			Connection connectBDD = connMgr.getConnection();
			// for now we only take into account one hour period
			String requestMessages = "SELECT co.message FROM `fr-checkOccupancy` co "
					+ "WHERE co.uid = ? AND co.timestampStart <= ? AND co.timestampEnd > ? ORDER BY co.message ASC";

			PreparedStatement query = connectBDD
					.prepareStatement(requestMessages);
			query.setString(1, uid);
			query.setLong(2, period.getTimeStampStart());
			query.setLong(3, period.getTimeStampStart());

			ResultSet result = query.executeQuery();

			while (result.next()) {
				messages.add(result.getString("message"));
			}
			return messages;
		} catch (SQLException e) {
			;
			e.printStackTrace();
			log(LOG_SIDE.SERVER, Level.SEVERE,
					"SQL error when getting user messages for period = "
							+ period + " uid = " + uid);
			return null;
		}
	}

	@Override
	public boolean registerUserSettings(RegisterUser user) throws TException {
		System.out.println(user);
		if (user == null) {
			return false;
		}

		if (user.getEmail() == null) {
			return false;
		}

		if (user.getConfig() == null) {
			return false;
		}

		try {
			Connection connectBDD = connMgr.getConnection();
			// for now we only take into account one hour period
			String insertReq = "INSERT INTO `fr-betaconfig`(email, config) VALUES(?,?)";
			PreparedStatement query = connectBDD.prepareStatement(insertReq);

			query.setString(1, user.getEmail());
			query.setString(2, user.getConfig());

			query.executeUpdate();
			log(Level.INFO,
					formatServerLogInfo("registerUserSettings",
							"email=" + user.getEmail() + "config==null?"
									+ (user.getConfig() == null)));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			log(LOG_SIDE.SERVER,
					Level.SEVERE,
					"SQL error when inserting user config email="
							+ user.getEmail() + "config == null ? "
							+ (user.getConfig() == null));
			// special case, we let the user test the app anyway
			return true;
		}
	}
}
