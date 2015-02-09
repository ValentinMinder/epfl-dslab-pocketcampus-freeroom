package org.pocketcampus.plugin.freeroom.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class allows you to either fetch and insert all the relevant rooms for
 * the projet in the database or fetching the details about one specific room.
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FetchRoomsDetails {

    private String URL_INDIVIDUAL_ROOM;
    private final String FILE_DINCAT = "src/org/pocketcampus/plugin/freeroom/server/data/locaux_din2.txt";

    private HashMap<String, String> dincat_textFR = null;
    private HashMap<String, String> dincat_textEN = null;

    private ConnectionManager connMgr = null;
    private Connection connDB = null;

    public FetchRoomsDetails(String db_url, String username, String passwd, String url_indiv) {
        connMgr = new ConnectionManager(db_url, username, passwd);
        URL_INDIVIDUAL_ROOM = url_indiv;
        this.connDB = null;

    }

    public FetchRoomsDetails(String url_indiv, Connection conn) {
        this.connDB = conn;
        URL_INDIVIDUAL_ROOM = url_indiv;
    }

    /**
     * Fetch and insert a particular room in the database.
     *
     * @param uid The unique identifier of the room to fetch
     */
    public boolean fetchRoomDetailInDB(String uid) {
        if (uid == null) {
            return false;
        }
        return insertIntoDBRoomDetail(fetchRoomDetail(uid));
    }

    /**
     * Fetch details about a room (i.e all its attributes) and convert it to a
     * JSONObject.
     *
     * @param uid The unique id of the room to fetch.
     * @return The JSONObject associated or null if an error occured (wrong id,
     * error...)
     */
    public JSONObject fetchRoomDetail(String uid) {
        StringBuilder mRoomDetail = new StringBuilder();
        URL url;
        InputStream is;
        BufferedReader bufferedInput;
        JSONObject mJSONRoom;

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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert a particular room in the database
     *
     * @param room The room to be inserted
     * @return true if the room has been successfully inserted
     */
    private boolean insertIntoDBRoomDetail(JSONObject room) {
        if (room == null) {
            return false;
        }

        Connection conn;
        try {
            if (connDB == null) {
                conn = connMgr.getConnection();
            } else {
                conn = connDB;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        String req = "INSERT INTO `fr-roomslist`("
                + "uid, doorCode, doorCodeWithoutSpace, alias, capacity, "
                + "site_label, surface, building_name, zone, unitlabel, "
                + "site_id, floor, unitname, site_name, unitid, building_label, "
                + "cf, adminuse, typeFR, typeEN, dincat, groupAccess) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE dincat = (?), typeFR = (?), typeEN = (?)";
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

            if (room.has("alias")) {
                query.setString(4, room.getString("alias"));
            } else {
                query.setNull(4, Types.CHAR);
            }

            if (room.has("places")) {
                query.setInt(5, room.getInt("places"));
            } else {
                query.setNull(5, Types.INTEGER);
            }

            if (room.has("site_label")) {
                query.setString(6, room.getString("site_label"));
            } else {
                query.setNull(6, Types.CHAR);
            }

            if (room.has("surface")) {
                query.setDouble(7, room.getDouble("surface"));
            } else {
                query.setNull(7, Types.DOUBLE);
            }

            if (room.has("building_name")) {
                query.setString(8, room.getString("building_name"));
            } else {
                query.setNull(8, Types.CHAR);
            }

            if (room.has("zone")) {
                query.setString(9, room.getString("zone"));
            } else {
                query.setNull(9, Types.CHAR);
            }

            if (room.has("unitlabel")) {
                query.setString(10, room.getString("unitlabel"));
            } else {
                query.setNull(10, Types.CHAR);
            }

            if (room.has("site_id")) {
                query.setInt(11, room.getInt("site_id"));
            } else {
                query.setNull(11, Types.INTEGER);
            }

            if (room.has("floor")) {
                query.setInt(12, room.getInt("floor"));
            } else {
                query.setNull(12, Types.INTEGER);
            }

            if (room.has("unitname")) {
                query.setString(13, room.getString("unitname"));
            } else {
                query.setNull(13, Types.CHAR);
            }

            if (room.has("site_name")) {
                query.setString(14, room.getString("site_name"));
            } else {
                query.setNull(14, Types.CHAR);
            }

            if (room.has("unitid")) {
                query.setInt(15, room.getInt("unitid"));
            } else {
                query.setNull(15, Types.INTEGER);
            }

            if (room.has("building_label")) {
                query.setString(16, room.getString("building_label"));
            } else {
                query.setNull(16, Types.CHAR);
            }

            if (room.has("cf")) {
                query.setString(17, room.getString("cf"));
            } else {
                query.setNull(17, Types.CHAR);
            }

            if (room.has("adminuse")) {
                query.setString(18, room.getString("adminuse"));
            } else {
                query.setNull(18, Types.CHAR);
            }

            if (room.has("dincat")) {
                String typeFR = getFromFileDinCatStringFR(room
                        .getString("dincat"));
                String typeEN = getFromFileDinCatStringEN(room
                        .getString("dincat"));
                query.setString(19, typeFR);
                query.setString(20, typeEN);
                query.setString(21, room.getString("dincat"));
                query.setInt(22,
                        Utils.determineGroupAccessRoom(room.getString("id")));
                // in case of update
                query.setString(23, room.getString("dincat"));
                query.setString(24, typeFR);
                query.setString(25, typeEN);
            } else {
                query.setNull(19, Types.CHAR);
                query.setNull(20, Types.CHAR);
                query.setNull(21, Types.CHAR);
                query.setInt(22,
                        Utils.determineGroupAccessRoom(room.getString("id")));
                query.setNull(23, Types.CHAR);
                query.setNull(24, Types.CHAR);
                query.setNull(25, Types.CHAR);
            }

            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get dincat plain text in french
     *
     * @param dincat The dincat code
     * @return The dincat plain text associated to the dincat code given
     */
    private String getFromFileDinCatStringFR(String dincat) {
        extractDinCatText();
        return dincat_textFR.get(dincat);
    }

    /**
     * Get dincat plain text in english
     *
     * @param dincat The dincat code
     * @return The dincat plain text associated to the dincat code given
     */
    private String getFromFileDinCatStringEN(String dincat) {
        extractDinCatText();
        return dincat_textEN.get(dincat);
    }

    /**
     * Extract dincat code and plaintext in both french and english. It updates
     * the HashMap dincat_textFR and dincat_textEN
     */
    private void extractDinCatText() {
        if (dincat_textFR == null || dincat_textEN == null) {
            dincat_textFR = new HashMap<String, String>();
            dincat_textEN = new HashMap<String, String>();
            try {
                Scanner sc = new Scanner(new File(FILE_DINCAT));

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] lineSplitted = line.split("[;]");
                    if (lineSplitted.length >= 5) {
                        dincat_textFR.put(lineSplitted[1], lineSplitted[3]);
                        dincat_textEN.put(lineSplitted[1], lineSplitted[4]);
                    } else {
                        System.err
                                .println("Cannot extract dincat plain text for "
                                        + line);
                    }
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
