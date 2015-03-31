package org.pocketcampus.plugin.freeroom.data;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl.OCCUPANCY_TYPE;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Fetch, insert, update rooms and/or occupancies from the ISA webservice
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FetchOccupancyDataJSON {
    private String URL_DATA;

    private final String KEY_ALIAS = "name";
    private final String KEY_ALIAS_WITHOUT_SPACE = "code";
    private final String KEY_UID = "sourceId";
    private final String KEY_CAPACITY = "capacity";
    private final String KEY_CAPACITY_EXTRA = "extraCapacity";

    private final String KEY_OCCUPANCY_START = "startDateTime";
    private final String KEY_OCCUPANCY_LENGTH = "duration";

    private ConnectionManager connMgr = null;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

    private FreeRoomServiceImpl server = null;

    private Connection connDB;

    public FetchOccupancyDataJSON(String db_url, String username,
                                  String passwd, FreeRoomServiceImpl server) {
        connMgr = new ConnectionManager(db_url, username, passwd);
        DB_URL = db_url;
        DB_USER = username;
        DB_PASSWORD = passwd;
        this.URL_DATA = server.getOCCUPANCIES_URL();
        this.server = server;
        this.connDB = null;

    }

    public FetchOccupancyDataJSON(FreeRoomServiceImpl server, Connection conn) {
        this.server = server;
        this.connDB = conn;
        this.URL_DATA = server.getOCCUPANCIES_URL();
    }

    /**
     * Fetch and insert in the database the occupancy for the given timestamp
     *
     * @param timestamp At what time to fetch
     */
    public void fetchAndInsert(long timestamp) {
        // String json = readFromFile("src" + File.separator + "freeroomjson");
        extractJSONAndInsert(fetch(timestamp), false);

    }

    /**
     * Fetch and insert in the database the occupancy during a certain period
     *
     * @param from The start of the period
     * @param to   The end of the period
     */
    public void fetchAndInsert(long from, long to) {
        if (to < from) {
            return;
        }
        extractJSONAndInsert(fetchFromTo(from, to), false);

    }

    /**
     * Fetch and insert in the database the rooms occupied during a given
     * period.
     *
     * @param from The start of the period
     * @param to   The end of the period
     */
    public void fetchAndInsertRoomsList(long from, long to) {
        extractJSONAndInsert(fetchFromTo(from, to), true);
    }

    /**
     * Parse the json page and insert the data contained in the database, it
     * first cleans the database if the connDB class object exists.
     *
     * @param jsonSource  The JSON to parse
     * @param updateRooms true if this method should also update/insert rooms, false
     *                    otherwise
     */
    private void extractJSONAndInsert(String jsonSource, boolean updateRooms) {
        if (connDB != null) {
            try {
                // clean the DATA
                connDB.setAutoCommit(false);
                server.cleanOldData(connDB);
            } catch (SQLException e) {
                e.printStackTrace();
                server.log(Level.SEVERE, "Cannot execute transaction while updating, in FetchOccupancyDataJSON");
                return;
            }
        }

        JsonParser parser = new JsonParser();
        JsonArray sourceArray = parser.parse(jsonSource).getAsJsonArray();
        int lengthSourceArray = sourceArray.size();

        for (int i = 0; i < lengthSourceArray; ++i) {
            JsonArray subArray = sourceArray.get(i).getAsJsonArray();
            int subArrayLength = subArray.size();

            if (subArrayLength == 2) {
                JsonObject room = subArray.get(0).getAsJsonObject();
                JsonArray occupancy = subArray.get(1).getAsJsonArray();

                String uid = extractAndInsertRoom(room, updateRooms);
                if (uid != null) {
                    extractAndInsertOccupancies(occupancy, uid);
                }
            }
        }
    }

    /**
     * Parse the given JSON representing a room and eventually update/insert it
     * into the database, depending of the argument updateRooms.
     *
     * @param room        The JSON describing the room
     * @param updateRooms true if you want to update/insert the room in the database,
     *                    false if you just want to get the UID
     * @return The unique id (UID) of the room
     */
    private String extractAndInsertRoom(JsonObject room, boolean updateRooms) {
        if (room == null) {
            return null;
        }

        try {
            String uid;
            if (room.has(KEY_UID) && !room.getAsJsonPrimitive(KEY_UID).isJsonNull()) {
                uid = room.get(KEY_UID).getAsString();
            } else {
                return null;
            }

            if (!updateRooms) {
                return uid;
            }

            // first fetch and insert the room from the other webservice
            FetchRoomsDetails frd;
            if (connDB == null) {
                frd = new FetchRoomsDetails(DB_URL, DB_USER, DB_PASSWORD,
                        server.getROOM_DETAILS_URL());
            } else {
                frd = new FetchRoomsDetails(server.getROOM_DETAILS_URL(), connDB);
            }

            if (!frd.fetchRoomDetailInDB(uid)) {
                return null;
            }

            Connection conn;
            if (connDB == null) {
                conn = connMgr.getConnection();
            } else {
                conn = connDB;
            }

            String reqCapacity = "UPDATE `fr-roomslist` SET capacity = ? WHERE uid = ? AND capacity = 0";
            String reqAlias = "UPDATE `fr-roomslist` SET alias = ? WHERE uid = ? AND alias IS NULL";
            PreparedStatement queryCapacity;
            PreparedStatement queryAlias;

            // first update the capacity
            queryCapacity = conn.prepareStatement(reqCapacity);

            int capacity = 0;
            if (room.has(KEY_CAPACITY)) {
                capacity = Integer.parseInt(room.get(KEY_CAPACITY).getAsString());
            }
            if (capacity == 0 && room.has(KEY_CAPACITY_EXTRA)) {
                capacity = Integer.parseInt(room.get(KEY_CAPACITY_EXTRA).getAsString());
            }

            queryCapacity.setInt(1, capacity);
            queryCapacity.setString(2, uid);
            queryCapacity.executeUpdate();

            // then update the alias
            queryAlias = conn.prepareStatement(reqAlias);

            String alias = null;
            if (room.has(KEY_ALIAS)) {
                JsonObject aliasObject = room.get(KEY_ALIAS).getAsJsonObject();
                if (aliasObject.has("fr")) {
                    alias = aliasObject.get("fr").getAsString();
                }
            }

            if (alias == null && room.has(KEY_ALIAS_WITHOUT_SPACE)) {
                alias = room.get(KEY_ALIAS_WITHOUT_SPACE).getAsString();
            }

            if (alias != null) {
                queryAlias.setString(1, alias);
                queryAlias.setString(2, uid);
                queryAlias.executeUpdate();
            }
            // if we are there, it means the fetch of the rooms details
            // succeeded
            return uid;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse and insert occupancies contained in the argument into the database
     *
     * @param array The array containing the occupancies for the given room
     * @param uid   The unique ID (UID) representing the rooms of the occupancies
     * @return The number of occupancies extracted and inserted into the
     * database
     */
    private int extractAndInsertOccupancies(JsonArray array, String uid) {
        if (array == null || uid == null) {
            return 0;
        }

        if (array.size() == 0) {
            return 0;
        }

        int nbOccupancy = array.size();
        int count = 0;
        for (int i = 0; i < nbOccupancy; ++i) {
            JsonObject occupancy = array.get(i).getAsJsonObject();
            long tsStart = 0;
            long tsEnd = 0;
            if (occupancy.has(KEY_OCCUPANCY_START)) {
                tsStart = Long.parseLong(occupancy
                        .get(KEY_OCCUPANCY_START).getAsString());

                if (occupancy.has(KEY_OCCUPANCY_LENGTH)) {
                    int length = Integer.parseInt(occupancy
                            .get(KEY_OCCUPANCY_LENGTH).getAsString());
                    tsEnd = tsStart + length * FRTimes.ONE_MIN_IN_MS;
                }
            }

            if (tsStart != 0 && tsEnd != 0 && tsStart < tsEnd) {
                FRPeriod period = new FRPeriod(tsStart, tsEnd);
                if (server.insertOccupancy(period, OCCUPANCY_TYPE.ROOM,
                        uid, null, null)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Fetch JSON page from ISA webservice during a given period
     *
     * @param from The start of the period
     * @param to   The end of the period
     * @return The JSON page fetched
     */
    private String fetchFromTo(long from, long to) {
        String timestampStringFrom = FRTimes.convertTimeStampInString(from);
        String timestampStringTo = FRTimes.convertTimeStampInString(to);
        return fetch(URL_DATA + timestampStringFrom + "/to/"
                + timestampStringTo);
    }

    /**
     * Fetch JSON page from ISA webservice for a given day
     *
     * @param timestamp The day to fetch
     * @return The JSON page fetched
     */
    private String fetch(long timestamp) {
        String timestampString = FRTimes.convertTimeStampInString(timestamp);
        return fetch(URL_DATA + timestampString);

    }

    /**
     * Fetch a JSON page at a given URL
     *
     * @param URL The URL to fetch
     * @return The JSON page located at the given URL, null if none
     */
    private String fetch(String URL) {
        try {
    		URLConnection conn = new URL(URL).openConnection();
    		conn.addRequestProperty("Accept", "application/json");
    		return StringUtils.fromStream(conn.getInputStream(), Charset.forName("UTF-8").name());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
