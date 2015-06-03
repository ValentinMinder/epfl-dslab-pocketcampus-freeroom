package org.pocketcampus.plugin.freeroom.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.freeroom.data.AutoUpdate;
import org.pocketcampus.plugin.freeroom.data.PeriodicallyUpdate;
import org.pocketcampus.plugin.freeroom.server.utils.CheckRequests;
import org.pocketcampus.plugin.freeroom.server.utils.OccupancySorted;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.*;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The actual implementation of the server side of the FreeRoom Plugin.
 * <p/>
 * It responds to different types of request from the clients.
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */

public class FreeRoomServiceImpl implements FreeRoomService.Iface {

    private final int LIMIT_AUTOCOMPLETE = 50;
    /**
     * some rooms don't have a capacity, so we display the occupation based on
     * 40 places, which is probably under-evaluated, so wont create many
     * issues...
     */
    int defaultCapacity = 40;

    private ConnectionManager connMgr;
    /**
     * Connection used for updates only.
     */
    private ConnectionManager connMgrUpdate;

    private SimpleDateFormat dateLogFormat = new SimpleDateFormat(
            "MMM dd,yyyy HH:mm");
    private Logger logger = Logger.getLogger(FreeRoomServiceImpl.class
            .getName());
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String OCCUPANCIES_URL;
    private String ROOMS_LIST_URL;
    private String ROOM_DETAILS_URL;

    private AutoUpdate updater;

    // be careful when changing this, it might lead to invalid data already
    // stored !
    // this is what is used to differentiate a room from a student occupation in
    // the DB.
    public enum OCCUPANCY_TYPE {
        ROOM, USER
    }

    // used to differentiate android log and server logs.
    private enum LOG_SIDE {
        ANDROID, SERVER
    }

    public FreeRoomServiceImpl() {
        logger.setLevel(Level.WARNING);
        ConsoleHandler logHandler = new ConsoleHandler();
        logger.addHandler(logHandler);

        DB_URL = PocketCampusServer.CONFIG.getString("DB_URL") + "?allowMultiQueries=true";
        DB_USER = PocketCampusServer.CONFIG.getString("DB_USERNAME");
        DB_PASSWORD = PocketCampusServer.CONFIG.getString("DB_PASSWORD");
        OCCUPANCIES_URL = PocketCampusServer.CONFIG.getString("FR_OCCUPANCIES");
        ROOMS_LIST_URL = PocketCampusServer.CONFIG.getString("FR_ROOMS_LIST");
        ROOM_DETAILS_URL = PocketCampusServer.CONFIG.getString("FR_ROOM_DETAILS");

        try {
            connMgr = new ConnectionManager(DB_URL, DB_USER, DB_PASSWORD, true);
            connMgrUpdate = new ConnectionManager(DB_URL, DB_USER, DB_PASSWORD, true);
            connMgr.getConnection().setTransactionIsolation(
                    Connection.TRANSACTION_READ_COMMITTED);
            connMgrUpdate.getConnection().setTransactionIsolation(
                    Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "Cannot start transaction mode read commited");
            e.printStackTrace();
        }

        updater = new AutoUpdate();

        // USEME: Periodically update
        // new Thread(new PeriodicallyUpdate(DB_URL, DB_USER, DB_PASSWORD,
        // this)).start();
        //
        // USEME: Rebuild rooms list in DB, need to tune parameter for tsStart
        // and tsEnd, (Start/End of semester)
        // Calendar mCalendar = Calendar.getInstance();
        // mCalendar.set(Calendar.MONTH, 8);
        // mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        // long tsStart = mCalendar.getTimeInMillis();
        // mCalendar.set(Calendar.MONTH, 11);
        // mCalendar.set(Calendar.DAY_OF_MONTH, 31);
        // long tsEnd = mCalendar.getTimeInMillis();
        // new Thread(new RebuildDB(DB_URL, DB_USER, DB_PASSWORD, this, tsStart,
        // tsEnd)).start();

    }

    public String getOCCUPANCIES_URL() {
        return OCCUPANCIES_URL;
    }

    public String getROOMS_LIST_URL() {
        return ROOMS_LIST_URL;
    }

    public String getROOM_DETAILS_URL() {
        return ROOM_DETAILS_URL;
    }

    // for test purposes ONLY
    public FreeRoomServiceImpl(ConnectionManager conn) {
        connMgr = conn;
    }

    public void log(Level level, String message) {
        log(LOG_SIDE.SERVER, level, message);
    }

    /**
     * Pre-format the message for logging
     *
     * @param message The message
     * @param path    The path to the file where the bug happened
     * @return A pre-formatted message containing the path and the message.
     */
    private String formatPathMessageLogAndroid(String message, String path) {
        return path + " / " + message;
    }

    /**
     * Pre-format the message for loggin
     *
     * @param method    The method to be logged
     * @param arguments The args of the method
     * @return A string suitable for logging
     */
    private String formatServerLogInfo(String method, String arguments) {
        return formatServerLogInfo(method, arguments, "NA");

    }

    /**
     * Pre-format the message for loggin
     *
     * @param method    The method to be logged
     * @param arguments The args of the method
     * @param answer    The return value of the method's call
     * @return A string suitable for logging
     */
    private String formatServerLogInfo(String method, String arguments,
                                       String answer) {
        return "ACTION=" + method + " / ARGS=" + arguments + " / RETURN="
                + answer;
    }

    /**
     * Logging function, time of the log will be set to the current timestamp.
     *
     * @param type    Indicates from where the log comes from (i.e android, server
     *                ...)
     * @param level   Level of the bug (e.g Level.SEVERE, Level.WARNING ...)
     * @param message Content of the logging message
     */
    private void log(LOG_SIDE type, Level level, String message) {
        log(type, level, message, System.currentTimeMillis());
    }

    /**
     * Logging function.
     *
     * @param type      Indicates from where the log comes from (i.e android, server
     *                  ...)
     * @param level     Level of the bug (e.g Level.SEVERE, Level.WARNING ...)
     * @param message   Content of the logging message
     * @param timestamp The time of the bug, might be different from the time when it
     *                  is called because log messages can come from various devices
     *                  (e.g android)
     */
    private void log(LOG_SIDE type, Level level, String message, long timestamp) {
        logger.log(level,
                "[" + type.toString() + "] " + dateLogFormat.format(timestamp)
                        + " : " + message);
    }

    /**
     * See insertOccupancyDetailedReply
     *
     * @return true if and only if insertOccupancyDetailedReply with the same
     * arguments return HTTP code 200 (OK)
     */
    public boolean insertOccupancy(FRPeriod period, OCCUPANCY_TYPE type,
                                   String uid, String hash, String userMessage) {
        return insertOccupancyDetailedReply(period, type, uid, hash,
                userMessage) == FRStatusCode.HTTP_OK;
    }

    /**
     * This method's job is to ensure the data are stored in a proper way.
     * Whenever you need to insert an occupancy you should call this method. The
     * start of an user occupancy should be a full hour (e.g 10h00). Timestamps
     * may be modified before insertion in the following ways : seconds and
     * milliseconds are set to 0, users occupancies are rounded to a full hour
     * before (10h05 -> 10h00). User occupancies are cut into chunks of one hour
     * if needed (e.g total length is more than one hour).
     *
     * @param period The period of the occupancy
     * @param type   Type of the occupancy (for instance user or room occupancy)
     * @param hash   null if ISA, id of user o/w
     * @return int error code defined by FRStatusCode, OK: insertion is
     * successful, BAD_REQUEST: argument required is (are) null,
     * PRECON_FAILED if the message of the user contains forbidden words
     * or is too long, INTERNAL_ERROR if the server failed at some point
     * and cannot answer properly.
     */
    public FRStatusCode insertOccupancyDetailedReply(FRPeriod period,
                                                     OCCUPANCY_TYPE type, String uid, String hash, String userMessage) {
        if (period == null || type == null || uid == null) {
            log(LOG_SIDE.SERVER,
                    Level.WARNING,
                    "Error during insertion of occupancy, at least one of the arguments is null : is null ? period = "
                            + (period == null)
                            + " type = "
                            + (type == null)
                            + " room = " + (uid == null));
            return FRStatusCode.HTTP_BAD_REQUEST;
        }
        // putting seconds and milliseconds to zero
        period.setTimeStampStart(FRTimes.roundSAndMSToZero(period
                .getTimeStampStart()));
        period.setTimeStampEnd(FRTimes.roundSAndMSToZero(period
                .getTimeStampEnd()));

        // hash is required for user occupancies (avoiding multiple submit from
        // same user)
        if (type == OCCUPANCY_TYPE.USER && hash == null) {
            log(LOG_SIDE.SERVER, Level.WARNING,
                    "Hash is null when inserting user occupancy");
            return FRStatusCode.HTTP_BAD_REQUEST;
        }

        boolean inserted;
        if (type == OCCUPANCY_TYPE.USER) {
            // round user occupancy to a full hour
            period.setTimeStampStart(FRTimes.roundHourBefore(period
                    .getTimeStampStart()));
            if (userMessage != null
                    && userMessage.length() > Constants.LENGTH_USERMESSAGE) {
                log(Level.INFO, "User message is too long, length = "
                        + userMessage.length());
                return FRStatusCode.HTTP_PRECON_FAILED;
            }
            inserted = insertOccupancyAndCheckOccupancyInDB(period, uid, type,
                    hash, userMessage);
        } else {
            inserted = insertOccupancyInDB(uid, period.getTimeStampStart(),
                    period.getTimeStampEnd(), type, 0);
        }

        log(LOG_SIDE.SERVER,
                Level.INFO,
                formatServerLogInfo(
                        "insertOccupancy",
                        "type=" + type.toString() + ",uid=" + uid + ",start="
                                + period.getTimeStampStart() + ",end="
                                + period.getTimeStampEnd() + ",userMessage="
                                + userMessage, inserted + ""));
        return inserted ? FRStatusCode.HTTP_OK
                : FRStatusCode.HTTP_INTERNAL_ERROR;

    }

    /**
     * Insert an occupancy in the database. It checks if there are no overlaps
     * between rooms occupancies. It assumes occupancies are correct as defined
     * in FRStatusCode insertOccupancyDetailedReply(...)
     *
     * @param period       The period of the occupancy
     * @param typeToInsert Specify the type of occupancy (USER, ROOM)
     * @param hash         The unique hash for each user, used to store an entry in the
     *                     checkOccupancy table to avoid multiple submissions for the
     *                     same period from an user
     * @return Return true if the occupancy has been successfully stored in the
     * database, false otherwise.
     */
    private boolean insertOccupancyAndCheckOccupancyInDB(FRPeriod period,
                                                         String uid, OCCUPANCY_TYPE typeToInsert, String hash,
                                                         String userMessage) {

        long tsStart = period.getTimeStampStart();
        long tsEnd = period.getTimeStampEnd();
        boolean userOccupation = typeToInsert == OCCUPANCY_TYPE.USER;

        if (!userOccupation) {
            return insertOccupancyInDB(uid, tsStart, tsEnd,
                    OCCUPANCY_TYPE.ROOM, 0);
        } else {

            // indicate whether the insertion has been successful or not.
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

                // if first insertion or update of room, insert a CHECK
                // occupancy (to avoid multiple submit from same users)
                insertOrUpdateCheckOccupancy(uid, hourSharpBefore + i
                                * FRTimes.ONE_HOUR_IN_MS, hash, prevRoom, prevMessage,
                        userMessage);
                if (prevRoom == null || !prevRoom.equals(uid)) {
                    // we increment the counter only if this is the first
                    // insertion (prevRoom == null) or if we change the
                    // room, in that case the counter for the new room has
                    // to be incremented and the old counter decremented.
                    if (prevRoom != null && !prevRoom.equals(uid)) {
                        decrementUserOccupancyCount(prevRoom, hourSharpBefore
                                + i * FRTimes.ONE_HOUR_IN_MS);
                    }
                    overallInsertion = overallInsertion
                            && insertOccupancyInDB(uid, hourSharpBefore + i
                                    * FRTimes.ONE_HOUR_IN_MS, hourSharpBefore
                                    + (i + 1) * FRTimes.ONE_HOUR_IN_MS,
                            OCCUPANCY_TYPE.USER, 1);
                }

            }

            return overallInsertion;
        }

    }

    /**
     * This method checks whether the user has already submitted something for
     * the same period, which is not allowed.
     *
     * @param period When the user submits its occupancy
     * @param room   The room in which the user occupancy will be counted
     * @param hash   The hash must be unique for each user and shouldn't depends on
     *               time.
     * @return The previous UID or null if none
     */
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

    /**
     * Get the last message stored for a given user (hash) at a given time and
     * for a given room (uid)
     *
     * @param tsStart The time of the message
     * @param uid     The uid of the room
     * @param hash    The unique identifier for each user
     * @return The last message stored for this user and this time and uid, null
     * if none
     */
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
     * want to insert a new occupancy call public insertOccupancy(...).
     *
     * @param uid         The uid of the room
     * @param tsStart     The start of the user occupancy
     * @param hash        The unique hash per user
     * @param prevRoom    The uid of the previous room being stored in the
     *                    checkOccupancy table (if one) null otherwise
     * @param prevMessage The previous message stored or null if none
     * @param userMessage The new message to store or null if no updates of the message
     *                    should be done.
     */
    private void insertOrUpdateCheckOccupancy(String uid, long tsStart,
                                              String hash, String prevRoom, String prevMessage, String userMessage) {

        if (prevRoom == null) {
            // first insertion, no problem with duplicate key, we insert
            insertCheckOccupancyInDB(uid, tsStart, hash, userMessage);
        } else {
            // otherwise we update the row
            String duplicateMessage;

            if (userMessage == null) {
                // no new message, take the old one
                duplicateMessage = prevMessage;
            } else {
                // new message, use the new one
                duplicateMessage = userMessage;
            }

            // no message stored should be null
            if (duplicateMessage == null) {
                duplicateMessage = "";
            }

            updateCheckOccupancyInDB(uid, prevRoom, tsStart, hash,
                    duplicateMessage);
        }

    }

    /**
     * Insert a new checkOccupancy in the database
     *
     * @param uid     The room of the occupancy
     * @param tsStart The time of the occupancy
     * @param hash    Which user insert a new occupancy
     * @param message The message to store, can be null
     */
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
            insertQuery.setString(5, message);
            insertQuery.setString(6, uid);
            int update = insertQuery.executeUpdate();

            if (update > 1) {
                // should not happen, it it happens, the method who called this
                // one has failed to check the state of the database
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
     * Update checkOccupancy attributs (used to deny multiple submits from
     * users).
     *
     * @param uid      The new uid
     * @param prevRoom The previous uid to be updated
     * @param tsStart  The timestamp of the record to update
     * @param hash     The hash of the record to update
     * @param message  The new message
     */
    private void updateCheckOccupancyInDB(String uid, String prevRoom,
                                          long tsStart, String hash, String message) {

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
        } catch (SQLException e) {
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error when writing check Occupancy for uid = " + uid
                            + " hash = " + hash + " start = " + tsStart);
            e.printStackTrace();
        }

    }

    /**
     * Decrement the count of users in the room by one.
     *
     * @param uid     The room to update
     * @param tsStart The period to update
     * @return true if successfully decremented
     */
    private boolean decrementUserOccupancyCount(String uid, long tsStart) {
        if (uid == null) {
            return false;
        }

        String updateRequest = "UPDATE `fr-occupancy-users` co SET co.count = co.count - 1 "
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
     * @param uid     The unique id of the room
     * @param tsStart The start of the period
     * @param tsEnd   The end of the period
     * @param type    The type of occupancy (USER, ROOM)
     * @param count   The count associated to the occupancy
     * @return If the query was successfull, false otherwise.
     */
    private boolean insertOccupancyInDB(String uid, long tsStart, long tsEnd,
                                        OCCUPANCY_TYPE type, int count) {
        String table = type == OCCUPANCY_TYPE.ROOM ? "`fr-occupancy`"
                : "`fr-occupancy-users`";
        String insertRequest = "INSERT INTO " + table
                + " (uid, timestampStart, timestampEnd, type, count) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE count = count + 1";

        Connection connectBDD;
        try {
            if (type == OCCUPANCY_TYPE.ROOM) {
                connectBDD = connMgrUpdate.getConnection();
            } else {
                connectBDD = connMgr.getConnection();
            }

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
     * Get the occupancy of a given period, for a specific list of rooms or for
     * any rooms. See thrift file for further informations about what can be
     * requested.
     */
    @Override
    public FROccupancyReply getOccupancy(FROccupancyRequest request)
            throws TException {
        if (request == null) {
            log(LOG_SIDE.SERVER, Level.WARNING, "Receiving null FRRequest");
            return new FROccupancyReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "FRRequest is null");
        }

        FROccupancyReply reply = CheckRequests.checkFRRequest(request);
        if (reply.getStatus() != FRStatusCode.HTTP_OK) {
            log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
            return reply;
        } else {
            reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
        }

        // check for updates
        if (updater.checkUpdate()) {
            Connection connUpdate;
            try {
                connUpdate = connMgrUpdate.getConnection();
                connUpdate
                        .setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                new Thread(new PeriodicallyUpdate(this, connUpdate))
                        .start();
            } catch (SQLException e) {
                log(Level.WARNING,
                        "Cannot create connection to the database for updating");
                e.printStackTrace();
            }
        }

        // round the given period to full hours to have a nice display on UI.
        FRPeriod period = request.getPeriod();
        period = FRTimes.roundFRRequestTimestamp(period);
        long tsStart = period.getTimeStampStart();
        long tsEnd = period.getTimeStampEnd();

        int group = request.getUserGroup();

        // FIXME should be set to false for students, true for staff
        boolean allowWeekends = false;
        boolean allowEvenings = false;

        String userLanguage = Utils.getSupportedLanguage(request
                .getUserLanguage());

        if (FRTimes.validCalendarsString(period, System.currentTimeMillis(),
                allowWeekends, allowEvenings).length() != 0) {
            // if something is wrong in the request
            return new FROccupancyReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "Bad timestamps! Your client sent a bad request, sorry");
        }

        boolean onlyFreeRoom = request.isOnlyFreeRooms();
        List<String> uidList = request.getUidList();

        HashMap<String, List<FRRoomOccupancy>> occupancies;

        if (uidList == null || uidList.isEmpty()) {
            if (onlyFreeRoom) {
                // we want to look into all the rooms
                occupancies = getOccupancyOfAnyFreeRoom(tsStart, tsEnd, group,
                        userLanguage);
            } else {
                return new FROccupancyReply(FRStatusCode.HTTP_BAD_REQUEST,
                        "The search for any free room must contains onlyFreeRoom = true");
            }
        } else {
            // or the user specified a specific list of rooms he wants to check
            occupancies = getOccupancyOfSpecificRoom(uidList, onlyFreeRoom,
                    tsStart, tsEnd, group, userLanguage);
        }

        if (occupancies == null) {
            return new FROccupancyReply(FRStatusCode.HTTP_INTERNAL_ERROR,
                    FRStatusCode.HTTP_INTERNAL_ERROR + "");
        }

        occupancies = Utils.sortRooms(occupancies);
        reply.setOccupancyOfRooms(occupancies);

        reply.setOverallTreatedPeriod(period);

        return reply;
    }

    /**
     * Return the occupancy of all the free rooms during a given period.
     *
     * @param tsStart       The start of the period, should be rounded, see public
     *                      getOccupancy
     * @param tsEnd         The end of the period, should be rounded, see public
     *                      getOccupancy
     * @return A HashMap organized as follows (building -> list of free rooms in
     * the building), null if an error occured
     */
    private HashMap<String, List<FRRoomOccupancy>> getOccupancyOfAnyFreeRoom(
            long tsStart, long tsEnd, int userGroup, String userLanguage) {

        Connection connectBDD;
        try {
            connectBDD = connMgr.getConnection();
            // first select rooms totally free

            String request = "SELECT rl.uid, rl.doorCode, rl.capacity "
                    + "FROM `fr-roomslist` rl "
                    + "WHERE rl.uid NOT IN("
                    + "SELECT ro.uid FROM `fr-occupancy` ro "
                    /*
                     * maybe simpler: (ro.timestampEnd >= tsStart AND
					 * ro.timestampStart <= tsEnd )
					 */
                    + "WHERE ((ro.timestampEnd <= ? AND ro.timestampEnd > ?) "
                    + "OR (ro.timestampStart < ? AND ro.timestampStart >= ?)"
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
                        "No rooms are free during period start = " + tsStart
                                + " end = " + tsEnd);
                return new HashMap<String, List<FRRoomOccupancy>>();
            }

            String logMessage = "tsStart=" + tsStart + ",tsEnd=" + tsEnd
                    + ",userGroup=" + userGroup;
            log(Level.INFO,
                    formatServerLogInfo("getOccupancyOfAnyFreeRoom", logMessage));
            return getOccupancyOfSpecificRoom(uidsList, true, tsStart, tsEnd,
                    userGroup, userLanguage);
        } catch (SQLException e) {
            e.printStackTrace();
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error for occupancy of any free room, start = "
                            + tsStart + " end = " + tsEnd);
            return null;
        }

    }

    /**
     * Return the occupancies of the specified list of rooms.
     *
     * @param uidList       The list of rooms to be checked
     * @param onlyFreeRooms If the results should contains only entirely free rooms or not
     * @param tsStart       The start of the period, should be rounded, see public
     *                      getOccupancy
     * @param tsEnd         The end of the period, should be rounded, see public
     *                      getOccupancy
     * @return A HashMap organized as follows (building -> list of rooms in the
     * building), null if an error occurred
     */
    private HashMap<String, List<FRRoomOccupancy>> getOccupancyOfSpecificRoom(
            List<String> uidList, boolean onlyFreeRooms, long tsStart,
            long tsEnd, int userGroup, String language) {

        uidList = Utils.removeDuplicate(uidList);

        String logMessage = "uidList=" + uidList + ",onlyFreeRooms="
                + onlyFreeRooms + ",tsStart=" + tsStart + ",tsEnd=" + tsEnd
                + ",userGroup=" + userGroup;

        int numberOfRooms = uidList.size();
        // formatting for the query
        String roomsListQueryFormat = "";
        for (int i = 0; i < numberOfRooms - 1; ++i) {
            roomsListQueryFormat += "?,";
        }
        roomsListQueryFormat += "?";
        Connection connectBDD;

        HashMap<String, List<FRRoomOccupancy>> result = new HashMap<String, List<FRRoomOccupancy>>();
        String[] tables = {"`fr-occupancy-users`", "`fr-occupancy`"};

        // Hashmap that stores all the results
        HashMap<String, OccupancySorted> tempResult = new HashMap<String, OccupancySorted>();

        try {
            connectBDD = connMgr.getConnection();

            for (String t : tables) {
                /**
                 * We have to get users occupancies and rooms occupancies two
                 * differents tables.
                 */
                String request = "SELECT rl.uid, rl.doorCode, rl.capacity, rl.alias, rl.surface, rl.type"
                        + language.toUpperCase()
                        + " AS typeRoom, "
                        + "uo.count, uo.timestampStart, uo.timestampEnd, uo.type "
                        + "FROM `fr-roomslist` rl, "
                        + t
                        + " uo "
                        + "WHERE rl.uid = uo.uid AND rl.uid IN("
                        + roomsListQueryFormat
                        + ") "
                        + "AND ((uo.timestampEnd <= ? AND uo.timestampEnd > ? ) "
                        + "OR (uo.timestampStart < ? AND uo.timestampStart >= ?)"
                        + "OR (uo.timestampStart <= ? AND uo.timestampEnd >= ?)) "
                        + "AND (uo.type = ? OR uo.count > 0) "
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
                query.setString(i + 6, OCCUPANCY_TYPE.ROOM.toString());

                ResultSet resultQuery = query.executeQuery();

                while (resultQuery.next()) {
                    // extract attributes of record
                    long start = resultQuery.getLong("timestampStart");
                    long end = resultQuery.getLong("timestampEnd");
                    String uid = resultQuery.getString("uid");
                    int count = resultQuery.getInt("count");
                    String doorCode = resultQuery.getString("doorCode");
                    String alias = resultQuery.getString("alias");
                    String typeLanguage = resultQuery.getString("typeRoom");
                    double surface = resultQuery.getDouble("surface");

                    OCCUPANCY_TYPE type = OCCUPANCY_TYPE.valueOf(resultQuery
                            .getString("type"));
                    boolean available = (type == OCCUPANCY_TYPE.USER) ? true
                            : false;
                    int capacity = resultQuery.getInt("capacity");

                    int calculCapacity = capacity > 0 ? capacity
                            : defaultCapacity;
                    double ratio = (double) count / calculCapacity;

                    FRPeriod period = new FRPeriod(start, end);
                    FRRoom mRoom = new FRRoom(doorCode, uid);
                    mRoom.setBuilding_name(Utils.extractBuilding(doorCode));

                    mRoom.setCapacity(capacity);
                    mRoom.setSurface(surface);
                    Utils.addAliasIfNeeded(mRoom, alias);

                    if (type != null) {
                        mRoom.setType(typeLanguage);
                    }

                    if (uid == null) {
                        continue;
                    }

                    OccupancySorted currentOS = tempResult.get(uid);
                    if (currentOS == null) {
                        currentOS = new OccupancySorted(mRoom, tsStart, tsEnd,
                                onlyFreeRooms);
                        tempResult.put(uid, currentOS);
                    }

                    FRPeriodOccupation accOcc = new FRPeriodOccupation(period,
                            available);
                    accOcc.setRatioOccupation(ratio);
                    currentOS.addActualOccupation(accOcc);
                }
            }

            for (Entry<String, OccupancySorted> occ : tempResult.entrySet()) {
                FRRoomOccupancy defOcc = occ.getValue().getOccupancy();
                addToHashMapOccupancy(occ.getValue().getRoom().getDoorCode(),
                        defOcc, result);
                uidList.remove(occ.getKey());
            }

            // if some rooms have no occupancies during the given period, we
            // need to cut the period into chunks of one hour for each room.
            if (!uidList.isEmpty()) {
                roomsListQueryFormat = "";
                for (int i = 0; i < uidList.size() - 1; ++i) {
                    roomsListQueryFormat += "?,";
                }

                // but we first need to get the additional info for each
                // room
                roomsListQueryFormat += "?";
                String infoRequest = "SELECT rl.uid, rl.doorCode, rl.capacity, rl.alias, rl.surface, rl.type"
                        + language.toUpperCase()
                        + " AS type "
                        + "FROM `fr-roomslist` rl "
                        + "WHERE rl.uid IN("
                        + roomsListQueryFormat + ")";

                PreparedStatement infoQuery = connectBDD
                        .prepareStatement(infoRequest);

                for (int i = 1; i <= uidList.size(); ++i) {
                    infoQuery.setString(i, uidList.get(i - 1));
                }

                ResultSet infoRoom = infoQuery.executeQuery();
                OccupancySorted currentOccupancy;

                while (infoRoom.next()) {
                    String uid = infoRoom.getString("uid");
                    String doorCode = infoRoom.getString("doorCode");
                    int capacity = infoRoom.getInt("capacity");
                    String alias = infoRoom.getString("alias");
                    String typeLanguage = infoRoom.getString("type");
                    double surface = infoRoom.getDouble("surface");

                    FRRoom mRoom = new FRRoom(doorCode, uid);
                    mRoom.setCapacity(capacity);
                    mRoom.setSurface(surface);
                    Utils.addAliasIfNeeded(mRoom, alias);
                    if (typeLanguage != null) {
                        mRoom.setType(typeLanguage);
                    }

                    currentOccupancy = new OccupancySorted(mRoom, tsStart,
                            tsEnd, onlyFreeRooms);
                    FRPeriod period = new FRPeriod(tsStart, tsEnd);
                    FRPeriodOccupation accOcc = new FRPeriodOccupation(period,
                            true);
                    currentOccupancy.addActualOccupation(accOcc);

                    FRRoomOccupancy mOccupancy = currentOccupancy
                            .getOccupancy();
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
     * @param doorCode The door code of the room to add
     * @param mOcc     The Occupancy of the room
     * @param result   The HashMap in which we add the room
     */
    private void addToHashMapOccupancy(String doorCode, FRRoomOccupancy mOcc,
                                       HashMap<String, List<FRRoomOccupancy>> result) {
        if (mOcc == null || doorCode == null) {
            return;
        }
        String building = Utils.extractBuilding(doorCode);
        List<FRRoomOccupancy> occ = result.get(building);

        if (occ == null) {
            occ = new ArrayList<FRRoomOccupancy>();
            result.put(building, occ);
        }
        occ.add(mOcc);
    }

    /**
     * Returns all the rooms that satisfies the hint given in the request.
     * <p/>
     * WARNING: if the request is set to "exactMatch", it will return only
     * result that have matched exactly. It you still want autocomplete in this
     * configuration, you can add a "%" in you constraint. In all other cases,
     * the server add the "%" automatically.
     * <p/>
     * The hint may be the start of the door code, the alias, the building name
     * or the room uid.
     * <p/>
     * Constraints should be at least 2 characters long. You can specify a list
     * of forbidden rooms the server should not include in the response. The
     * number of results is bounded by the constant LIMIT_AUTOCOMPLETE.
     */
    @Override
    public FRAutoCompleteReply autoCompleteRoom(FRAutoCompleteRequest request)
            throws TException {
        if (request == null) {
            log(LOG_SIDE.SERVER, Level.WARNING,
                    "Receiving null AutoCompleteRequest");
            return new FRAutoCompleteReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "AutocompleteRequest is null");
        }

        FRAutoCompleteReply reply = CheckRequests
                .checkAutoCompleteRequest(request);
        if (reply.getStatus() != FRStatusCode.HTTP_OK) {
            log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
            return reply;
        } else {
            reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
        }

        String constraint = request.getConstraint();
        String userLanguage = Utils.getSupportedLanguage(request
                .getUserLanguage());

        if (constraint.length() < Constants.MIN_AUTOCOMPL_LENGTH) {
            return new FRAutoCompleteReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "Constraints should be at least "
                            + Constants.MIN_AUTOCOMPL_LENGTH
                            + " characters long.");
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

        // if we want not exact match, we add a "%"
        if (request.isSetExactString() && request.isExactString()) {
            // the constraint is not changed (user may have entered a %!)
        } else {
            constraint += "%";
        }

        try {
            Connection connectBDD = connMgr.getConnection();
            String requestSQL;
            if (forbiddenRooms == null || forbiddenRooms.isEmpty()) {
                requestSQL = "SELECT * "
                        + "FROM `fr-roomslist` rl "
                        + "WHERE (rl.uid LIKE (?) OR rl.doorCodeWithoutSpace LIKE (?) OR rl.alias LIKE (?) OR rl.building_name LIKE (?)) "
                        + "AND rl.groupAccess <= ? AND rl.enabled = 1 "
                        + "ORDER BY rl.doorCode ASC LIMIT "
                        + LIMIT_AUTOCOMPLETE;
            } else {
                requestSQL = "SELECT * "
                        + "FROM `fr-roomslist` rl "
                        + "WHERE (rl.uid LIKE (?) OR rl.doorCodeWithoutSpace LIKE (?) OR rl.alias LIKE (?) OR rl.building_name LIKE (?)) "
                        + "AND rl.groupAccess <= ? AND rl.enabled = 1 AND rl.uid NOT IN ("
                        + forbidRoomsSQL + ") "
                        + "ORDER BY rl.doorCode ASC LIMIT "
                        + LIMIT_AUTOCOMPLETE;
            }

            PreparedStatement query = connectBDD.prepareStatement(requestSQL);
            query.setString(1, constraint);
            query.setString(2, constraint);
            query.setString(3, constraint);
            query.setString(4, constraint);
            query.setInt(5, request.getUserGroup());

            if (forbiddenRooms != null) {
                int i = 6;
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
                if (cap >= 0) {
                    frRoom.setCapacity(cap);
                }
                double surface = resultQuery.getDouble("surface");
                frRoom.setSurface(surface);
                String alias = resultQuery.getString("alias");
                Utils.addAliasIfNeeded(frRoom, alias);

                String typeFR = resultQuery.getString("typeFR");
                if (typeFR != null && userLanguage.equals("fr")) {
                    frRoom.setType(typeFR);
                }

                String typeEN = resultQuery.getString("typeEN");
                if (typeEN != null
                        && (userLanguage.equals("en") || userLanguage
                        .equals(Utils.defaultLanguage))) {
                    frRoom.setType(typeEN);
                }
                rooms.add(frRoom);
            }

            reply = new FRAutoCompleteReply(FRStatusCode.HTTP_OK, ""
                    + HttpURLConnection.HTTP_OK);
            reply.setListRoom(Utils.sortRoomsByBuilding(rooms));

            String logMessage = "constraint=" + constraint + ",forbiddenRooms="
                    + forbiddenRooms;
            log(Level.INFO, formatServerLogInfo("autoCompleteRoom", logMessage));
        } catch (SQLException e) {
            reply = new FRAutoCompleteReply(FRStatusCode.HTTP_INTERNAL_ERROR,
                    "" + HttpURLConnection.HTTP_INTERNAL_ERROR);
            e.printStackTrace();
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error for autocomplete request with constraint "
                            + constraint);
        }
        return reply;
    }

    // @Override
    // public FRAutoCompleteUserMessageReply autoCompleteUserMessage(
    // FRAutoCompleteUserMessageRequest request) throws TException {
    // if (request == null) {
    // log(LOG_SIDE.SERVER, Level.WARNING,
    // "Receiving null AutoCompleteUserMessageRequest");
    // return new FRAutoCompleteUserMessageReply(
    // FRStatusCode.HTTP_BAD_REQUEST,
    // "AutocompleteUserMessageRequest is null");
    // }
    //
    // FRAutoCompleteUserMessageReply reply = CheckRequests
    // .checkAutoCompleteUserMessageRequest(request);
    // if (reply.getStatus() != FRStatusCode.HTTP_OK) {
    // log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
    // return reply;
    // } else {
    // reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
    // }
    //
    // String constraint = request.getConstraint().replaceAll("\\s+", "");
    // String uid = request.getRoom().getUid();
    // FRPeriod period = request.getPeriod();
    //
    // if (period.getTimeStampEnd() < period.getTimeStampStart()) {
    // return new FRAutoCompleteUserMessageReply(
    // FRStatusCode.HTTP_BAD_REQUEST,
    // "The end of the period should be after the start");
    // }
    //
    // String requestSQL = "SELECT co.message "
    // + "FROM `fr-checkOccupancy` co "
    // + "WHERE co.uid = ? AND co.timestampStart >= ? AND co.timestampEnd <= ? "
    // + "AND LOWER(co.message) LIKE (?) ORDER BY co.message ASC";
    //
    // try {
    // ArrayList<String> messages = new ArrayList<String>();
    //
    // Connection connectBDD = connMgr.getConnection();
    //
    // PreparedStatement query = connectBDD.prepareStatement(requestSQL);
    // query.setString(1, uid);
    // query.setLong(2, period.getTimeStampStart());
    // query.setLong(3, period.getTimeStampEnd());
    // query.setString(4, "%" + constraint.toLowerCase() + "%");
    //
    // ResultSet result = query.executeQuery();
    //
    // while (result.next()) {
    // messages.add(result.getString("message"));
    // }
    //
    // String logMessage = "constraint=" + constraint;
    // log(Level.INFO,
    // formatServerLogInfo("autoCompleteUserMessage", logMessage));
    // reply.setMessages(messages);
    // } catch (SQLException e) {
    // ;
    // e.printStackTrace();
    // log(LOG_SIDE.SERVER, Level.SEVERE,
    // "SQL error when autocompleting user message for uid = "
    // + uid + " period = " + period + " constraint = "
    // + constraint);
    // return new FRAutoCompleteUserMessageReply(
    // FRStatusCode.HTTP_INTERNAL_ERROR,
    // "Error when autocompleting");
    // }
    //
    // return reply;
    // }

    /**
     * The client can specify a user occupancy during a given period, multiple
     * submits for the same period (and same user) are not allowed, we return a
     * HTTP_CONFLICT in that case.
     */
    @Override
    public FRImWorkingReply indicateImWorking(FRImWorkingRequest request)
            throws TException {
        if (request == null) {
            log(LOG_SIDE.SERVER, Level.WARNING,
                    "Receiving null ImWorkingRequest");
            return new FRImWorkingReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "ImWorkingReply is null");
        }

        FRImWorkingReply reply = CheckRequests.checkImWorkingRequest(request);
        if (reply.getStatus() != FRStatusCode.HTTP_OK) {
            log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
            return reply;
        } else {
            reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
        }

        FRWorkingOccupancy work = request.getWork();
        FRPeriod period = work.getPeriod();
        String userMessage = (work.isSetMessage() && work.getMessage() != null && work
                .getMessage().trim().length() > 0) ? work.getMessage().trim()
                : null;
        FRRoom room = work.getRoom();
        FRStatusCode code = insertOccupancyDetailedReply(period,
                OCCUPANCY_TYPE.USER, room.getUid(), request.getHash(),
                userMessage);

        if (code == FRStatusCode.HTTP_OK) {
            String logMessage = "start=" + period.getTimeStampStart() + ",end="
                    + period.getTimeStampEnd() + ",uid= " + room.getUid()
                    + ",hash=" + request.getHash() + ",userMessage="
                    + userMessage;
            log(Level.INFO,
                    formatServerLogInfo("indicateImWorking", logMessage));
        }
        return new FRImWorkingReply(code, " ");

    }

    @Override
    public FRWhoIsWorkingReply getUserMessages(FRWhoIsWorkingRequest request)
            throws TException {
        if (request == null) {
            log(LOG_SIDE.SERVER, Level.WARNING,
                    "Receiving null WhoIsWorkingRequest");
            return new FRWhoIsWorkingReply(FRStatusCode.HTTP_BAD_REQUEST,
                    "WhoIsWorkingRequest is null");
        }

        FRWhoIsWorkingReply reply = CheckRequests
                .checkWhoIsWorkingRequest(request);
        if (reply.getStatus() != FRStatusCode.HTTP_OK) {
            log(LOG_SIDE.SERVER, Level.WARNING, reply.getStatusComment());
            return reply;
        } else {
            reply.setStatusComment(HttpURLConnection.HTTP_OK + "");
        }

        FRPeriod period = request.getPeriod();

        List<String> listMessages = getUserMessages(period,
                request.getRoomUID());
        if (listMessages == null) {
            return new FRWhoIsWorkingReply(FRStatusCode.HTTP_INTERNAL_ERROR,
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
     * @param period The period of the search
     * @param uid    The room of the search
     * @return A list of messages with the messages or empty list if none, null
     * if an error occured
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
            e.printStackTrace();
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error when getting user messages for period = "
                            + period + " uid = " + uid);
            return null;
        }
    }

    /**
     * Clean old data, used when updating, from the current day included.
     */
    public void cleanOldData(Connection connDB) {
        try {
            Connection conn;
            if (connDB == null) {
                conn = connMgr.getConnection();
            } else {
                conn = connDB;
            }

            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR, 0);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
            long startDay = mCalendar.getTimeInMillis();

            String cleanRequest = "DELETE FROM `fr-occupancy` WHERE timestampStart > ?";
            PreparedStatement cleanQuery = conn.prepareStatement(cleanRequest);

            cleanQuery.setLong(1, startDay);
            cleanQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error when cleaning old data in the database");
        }
    }

    /**
     * Clean the rooms list stored in the database, used when rebuilding the
     * entire database.
     */
    public void cleanRoomsList() {
        try {
            Connection connectBDD = connMgr.getConnection();
            String cleanRequest = "DELETE FROM `fr-roomslist` WHERE 1";

            PreparedStatement query = connectBDD.prepareStatement(cleanRequest);

            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log(LOG_SIDE.SERVER, Level.SEVERE,
                    "SQL error when cleaning rooms list in the database");
        }
    }
}
