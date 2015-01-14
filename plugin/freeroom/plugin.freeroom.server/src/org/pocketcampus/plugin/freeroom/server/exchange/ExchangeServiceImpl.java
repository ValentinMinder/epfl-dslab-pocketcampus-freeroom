package org.pocketcampus.plugin.freeroom.server.exchange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl.OCCUPANCY_TYPE;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * This class is used to fetch data and insert it into the database from the EWA
 * platform.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class ExchangeServiceImpl {

	private ConnectionManager connMgr = null;
	private FreeRoomServiceImpl server = null;
	private Connection connDB = null;

	public ExchangeServiceImpl(String db_url, String username, String passwd,
			FreeRoomServiceImpl server) {
		connMgr = new ConnectionManager(db_url, username, passwd);
		this.server = server;
		this.connDB = null;
	}

	public ExchangeServiceImpl(FreeRoomServiceImpl server, Connection conn) {
		this.connDB = conn;
		this.server = server;
	}

	/**
	 * Reset all the exchange ids to NULL.
	 * 
	 * @return
	 */
	private boolean resetExchangeData() {
		Connection conn = null;
		try {
			if (connDB == null) {
				conn = connMgr.getConnection();
			} else {
				conn = connDB;
			}
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
			if (connDB == null) {
				conn = connMgr.getConnection();
			} else {
				conn = connDB;
			}
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
			if (connDB == null) {
				conn = connMgr.getConnection();
			} else {
				conn = connDB;
			}
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
			if (connDB == null) {
				conn = connMgr.getConnection();
			} else {
				conn = connDB;
			}
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
	 * EWAid set. It calls updateEWAOccupancy with a default time window of 2
	 * weeks.
	 * 
	 * @return true if successful for all the rooms, false if an error occured.
	 */
	public boolean updateEWAOccupancy2Weeks() {
		// to be set to same window as permitted by server and clients
		long timeStampStart = System.currentTimeMillis()
				- FRTimes.ONE_WEEK_IN_MS;
		long timeStampEnd = System.currentTimeMillis() + FRTimes.ONE_WEEK_IN_MS;
		FRPeriod mFrPeriod = new FRPeriod(timeStampStart, timeStampEnd);
		return updateEWAOccupancy(mFrPeriod);
	}

	/**
	 * Retrieves the occupancies from Exchange for all the rooms that have an
	 * EWAid set. It calls updateEWAOccupancy with time window defined by from
	 * and to arguments.
	 * 
	 * @param from
	 *            The start of the period to update
	 * @param to
	 *            The end of the period to update
	 * @return true if successful for all the rooms, false if an error occured.
	 */
	public boolean updateEWAOccupancyFromTo(long from, long to) {
		if (to < from) {
			return false;
		}
		return updateEWAOccupancy(new FRPeriod(from, to));
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
					if (connDB == null) {
						conn = connMgr.getConnection();
					} else {
						conn = connDB;
					}
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
						if (server != null) {
							server.insertOccupancy(mPeriod,
									OCCUPANCY_TYPE.ROOM, room.getUid(), null,
									null);
						}
					}
					// query.execute();
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
			if (connDB == null) {
				conn = connMgr.getConnection();
			} else {
				conn = connDB;
			}
			PreparedStatement query;
			String b = "DELETE FROM `fr-occupancy` WHERE uid = ? AND type = ?";
			query = conn.prepareStatement(b);
			query.setString(1, uid);
			query.setString(2, OCCUPANCY_TYPE.ROOM.toString());
			query.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
}
