package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
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

import android.util.Log;

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

	// be careful when changing this, it might lead to invalid data already
	// stored !
	// this is what is used to differentiate a room from a student occupation in
	// the DB.
	public enum OCCUPANCY_TYPE {
		ROOM, USER;
	};

	// margin for error is 15 minute
	private final long MARGIN_ERROR_TIMESTAMP = 60 * 1000 * 15;
	private final long ONE_HOUR_MS = 3600 * 1000;

	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ... V2");
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
				PC_SRV_CONFIG.getString("DB_PASSWORD"), this);

		// update ewa : should be done periodically...
		boolean updateEWA = false;
		if (updateEWA) {
			if (mExchangeService.updateEWAOccupancy()) {
				System.out.println("EWA data succesfully updated!");
			} else {
				System.err.println("EWA data couldn't be completely loaded!");
			}
		}

	}

	/**
	 * This method's job is to ensure the data are stored in a proper way.
	 * Whenever you need to insert an occupancy you should call this one. *
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
			FRRoom room) {
		return insertAndCheckOccupancyRoom(period, room, type);
	}

	private boolean insertAndCheckOccupancyRoom(FRPeriod period, FRRoom room,
			OCCUPANCY_TYPE typeToInsert) {
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		boolean userOccupation = (typeToInsert == OCCUPANCY_TYPE.USER) ? true : false;

		// first check if you can fully insert it (no other overlapping
		// occupancy of rooms)
		String checkRequest = "SELECT * FROM `fr-occupancy` oc "
				+ "WHERE (oc.timestampStart <= ? AND oc.timestampStart >= ?) "
				+ "OR (oc.timestampEnd >= ? AND oc.timestampEnd <= ?) "
				+ "OR (oc.timestampStart >= ? AND oc.timestampEnd <= ?)";

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

			ResultSet checkResult = checkQuery.executeQuery();

			while (checkResult.next()) {
				OCCUPANCY_TYPE type = OCCUPANCY_TYPE.valueOf(checkResult
						.getString("type"));
				String uid = checkResult.getString("uid");
				long start = checkResult.getLong("timestampStart");
				long end = checkResult.getLong("timestampEnd");

				// if we have a match and this is a room occupancy, we cannot go
				// further there is an overlap
				if (typeToInsert == OCCUPANCY_TYPE.ROOM
						&& type == OCCUPANCY_TYPE.ROOM) {
					System.out
							.println("Error during insertion of occupancy, overlapping of two rooms occupancy.");
					return false;
					// } else if (typeToInsert == OCCUPANCY_TYPE.ROOM) {
					// // else, we need to adapt the boundaries of the
					// overlapping
					// // entries, rooms occupancy has the priority over user
					// // occupancy
					//
					// if (start > tsStart && start < tsEnd) {
					// adaptTimeStampOccupancy(uid, start, tsEnd, end,
					// OCCUPANCY_TYPE.USER);
					// } else if (end < tsEnd && end > start) {
					// adaptTimeStampOccupancy(uid, start, start, tsStart,
					// OCCUPANCY_TYPE.USER);
					// } else {
					// // simply delete it if it is entirely in the period
					// deleteOccupancy(uid, start);
					// }
					// } else if (typeToInsert == OCCUPANCY_TYPE.USER
					// && type == OCCUPANCY_TYPE.ROOM) {
					// // simply adapt our boundaries
					// userOccupation = true;
					// if (start > tsStart && start < tsEnd) {
					// tsEnd = start;
					// maxEnd = start;
					// } else if (end < tsEnd && end > tsStart) {
					// tsStart = end;
					// minStart = end;
					// } else {
					// // there is a course, no possiblity to insert a user
					// // occupancy here
					// return false;
					// }
					// } else if (typeToInsert == OCCUPANCY_TYPE.USER){
					// // TODO check how user occupancies is updated to see if
					// it
					// // is worth keeping this branchment
					// if (start > tsStart && start < tsEnd) {
					// // shouldn't happen
					// System.out
					// .println("Error while inserting, trying to insert user occupancy that PARTIALLY overlap another useroccupancy");
					// return false;
					// } else if (end < tsEnd && end > start) {
					// // shouldn't happen
					// System.out
					// .println("Error while inserting, trying to insert user occupancy that PARTIALLY overlap another useroccupancy");
					//
					// return false;
					// }
					// // otherwise no problem, insertion is step by step
					// }
				}
			}
			// and now insert it !

			if (!userOccupation) {
				return insertOccupancyInDB(room.getUid(), tsStart, tsEnd,
						OCCUPANCY_TYPE.ROOM, 0);
			} else {
				if (tsEnd - tsStart < ONE_HOUR_MS) {
					System.out.println("occupancy less than a hour");
//					return false;
				}

				boolean overallInsertion = true;

				long hourSharpBefore = Utils.roundHourBefore(tsStart);
				long numberHours = Utils.determineNumberHour(tsStart, tsEnd);
				
				for (int i = 0; i < numberHours; ++i) {
					overallInsertion = overallInsertion
							&& insertOccupancyInDB(room.getUid(),
									hourSharpBefore + i * ONE_HOUR_MS,
									hourSharpBefore + (i + 1) * ONE_HOUR_MS,
									OCCUPANCY_TYPE.USER, 1);
				}

				return overallInsertion;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

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
			e.printStackTrace();
			return false;
		}
	}

	private void adaptTimeStampOccupancy(String uid, long tsStart,
			long newStart, long newEnd, OCCUPANCY_TYPE type) {
		// if we want to adapt a user occupancy, we need to check that the
		// resized bounds fits the condition to have 30min at least per entry
		if (type == OCCUPANCY_TYPE.USER) {
			// TODO
		}
		String request = "UPDATE `fr-occupancy` "
				+ "SET timestampStart = ? AND timestampEnd = ? "
				+ "WHERE uid = ? AND timestampStart = ?";
		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD.prepareStatement(request);
			query.setLong(1, newStart);
			query.setLong(2, newEnd);
			query.setString(3, uid);
			query.setLong(4, tsStart);

			query.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteOccupancy(String uid, long tsStart) {
		String request = "DELETE FROM `fr-occupancy` WHERE uid = ? AND timestampStart = ?";
		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD.prepareStatement(request);
			query.setString(1, uid);
			query.setLong(2, tsStart);

			query.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
	}

	@Override
	public FreeRoomReply getFreeRoomFromTime(FreeRoomRequest request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OccupancyReply checkTheOccupancy(OccupancyRequest request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FRReply getOccupancy(FRRequest request) throws TException {
		FRReply reply = new FRReply(HttpURLConnection.HTTP_OK,
				HttpURLConnection.HTTP_OK + "");

		FRPeriod period = request.getPeriod();
		long tsStart = Utils.roundToNearestHalfHourBefore(period
				.getTimeStampStart());
		long tsEnd = Utils
				.roundToNearestHalfHourAfter(period.getTimeStampEnd());

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
			occupancies = getOccupancyOfAnyFreeRoom(onlyFreeRoom, tsStart,
					tsEnd);
		} else {
			// or the user specified a specific list of rooms he wants to check
			occupancies = getOccupancyOfSpecificRoom(uidList, onlyFreeRoom,
					tsStart, tsEnd);
		}

		reply.setOccupancyOfRooms(occupancies);
		return reply;
	}

	private HashMap<String, List<Occupancy>> getOccupancyOfAnyFreeRoom(
			boolean onlyFreeRooms, long tsStart, long tsEnd) {
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
						+ "AND ro.type LIKE ?)";

				PreparedStatement query = connectBDD.prepareStatement(request);
				query.setLong(1, tsEnd);
				query.setLong(2, tsStart);
				query.setLong(3, tsEnd);
				query.setLong(4, tsStart);
				query.setLong(5, tsStart);
				query.setLong(6, tsEnd);
				query.setString(7, OCCUPANCY_TYPE.ROOM.toString());

				ResultSet resultQuery = query.executeQuery();
				HashMap<String, FRRoom> rooms = new HashMap<String, FRRoom>();
				String roomsFreeSQL = "";

				while (resultQuery.next()) {
					if (!rooms.isEmpty()) {
						roomsFreeSQL += ",";
					}

					String uid = resultQuery.getString("uid");
					String doorCode = resultQuery.getString("doorCode");
					int capacity = resultQuery.getInt("capacity");

					FRRoom mRoom = new FRRoom(doorCode, uid);
					mRoom.setCapacity(capacity);

					rooms.put(uid, mRoom);
					roomsFreeSQL += uid;
				}

				// and also select user occupancy of these rooms
				String userOccupancyRequest = "SELECT "
						+ "uo.uid, uo.count, uo.timestampStart, uo.timestampEnd "
						+ "FROM `fr-occupancy` uo "
						+ "WHERE uo.uid IN("
						+ roomsFreeSQL
						+ ") "
						+ "AND uo.type LIKE ? "
						+ "AND ((uo.timestampEnd <= ? AND uo.timestampEnd >= ? ) "
						+ "OR (uo.timestampStart <= ? AND uo.timestampStart >= ?)"
						+ "OR (uo.timestampStart <= ? AND uo.timestampEnd >= ?)) "
						+ "ORDER BY uo.uid ASC, uo.timestampStart ASC";

				PreparedStatement queryUser = connectBDD
						.prepareStatement(userOccupancyRequest);

				queryUser.setString(1, OCCUPANCY_TYPE.USER.toString());
				queryUser.setLong(2, tsEnd);
				queryUser.setLong(3, tsStart);
				queryUser.setLong(4, tsEnd);
				queryUser.setLong(5, tsStart);
				queryUser.setLong(6, tsStart);
				queryUser.setLong(7, tsEnd);

				ResultSet occupancyResult = queryUser.executeQuery();

				String currentUID = null;
				String currentDoorCode = null;
				OccupancySorted currentOccupancy = null;

				// and now extract and create occupancies for each rooms
				// query beeing sorted by UID and then by timestampStart, we
				// don't need to access at each iteration the room stored in
				// rooms
				// hashmap, only when there is a change. And also we can add the
				// actualoccupation as they come, (sorted by timestamp)
				while (occupancyResult.next()) {
					// extract attributes of record
					long start = resultQuery.getLong("timestampStart");
					long end = resultQuery.getLong("timestampEnd");
					String uid = resultQuery.getString("uid");
					int count = resultQuery.getInt("count");

					FRPeriod period = new FRPeriod(start, end, false);

					// if this is the first iteration
					if (currentUID == null) {
						System.out.println("first iteration");
						FRRoom mRoom = rooms.get(uid);
						currentUID = uid;
						currentDoorCode = mRoom.getDoorCode();
						currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, true);
					}

					// we move on to the next room thus re-initialize attributes
					// for the loop, as well as storing the previous room in the
					// result hashmap
					if (!uid.equals(currentUID)) {
						System.out.println("new room ");
						Occupancy mOccupancy = currentOccupancy.getOccupancy();

						addToHashMapOccupancy(currentDoorCode, mOccupancy, result);

						// remove the room from the list
						rooms.remove(currentUID);

						// re-initialize the value, and continue the process for
						// other rooms
						FRRoom mRoom = rooms.get(uid);
						currentDoorCode = mRoom.getDoorCode();
						currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, true);
						currentUID = uid;
					}

					ActualOccupation accOcc = new ActualOccupation(period, true);
					accOcc.setProbableOccupation(count);
					currentOccupancy.addActualOccupation(accOcc);
				}

				// the last room has not been added yet
				if (currentOccupancy != null && currentOccupancy.size() != 0) {
					Occupancy mOccupancy = currentOccupancy.getOccupancy();

					addToHashMapOccupancy(currentDoorCode, mOccupancy, result);

					// remove the room from the list
					rooms.remove(currentUID);
				}

				// and finally, check if there is some free rooms left that have
				// no user occupancy and need manual action (i.e set ratio to 0
				// and has to be added in the result hashmap)

				for (FRRoom mRoom : rooms.values()) {
					currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, true);
					FRPeriod period = new FRPeriod(tsStart, tsEnd, false);
					ActualOccupation accOcc = new ActualOccupation(period, true);
					accOcc.setProbableOccupation(0);
					currentOccupancy.addActualOccupation(accOcc);
					
					Occupancy mOccupancy = currentOccupancy.getOccupancy();
					addToHashMapOccupancy(mRoom.getDoorCode(), mOccupancy, result);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private void addToHashMapOccupancy(String doorCode, Occupancy mOcc,
			HashMap<String, List<Occupancy>> result) {
		if (mOcc == null) {
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

	private HashMap<String, List<Occupancy>> getOccupancyOfSpecificRoom(
			List<String> uidList, boolean onlyFreeRooms, long tsStart,
			long tsEnd) {
		// TODO only Free Rooms
		if (uidList.isEmpty()) {
			return getOccupancyOfAnyFreeRoom(onlyFreeRooms, tsStart, tsEnd);
		}

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
			String request = "SELECT rl.uid, rl.doorCode, rl.capacity, "
					+ "uo.count, uo.timestampStart, uo.timestampEnd, uo.type "
					+ "FROM `fr-roomslist` rl, `fr-occupancy` uo "
					+ "WHERE rl.uid = uo.uid AND rl.uid IN("
					+ roomsListQueryFormat + ") "
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

			// and now extract and create occupancies for each rooms
			// query beeing sorted by UID and then by timestampStart, we
			// don't need to access at each iteration the room stored in
			// rooms
			// hashmap, only when there is a change. And also we can add the
			// actualoccupation as they come, (sorted by timestamp)
			while (resultQuery.next()) {
				// extract attributes of record
				long start = resultQuery.getLong("timestampStart");
				long end = resultQuery.getLong("timestampEnd");
				String uid = resultQuery.getString("uid");
				int count = resultQuery.getInt("count");
				String doorCode = resultQuery.getString("doorCode");
				OCCUPANCY_TYPE type = OCCUPANCY_TYPE.valueOf(resultQuery.getString("type"));
				boolean available = (type == OCCUPANCY_TYPE.USER) ? true : false;
				int capacity = resultQuery.getInt("capacity");
				double ratio = capacity > 0 ? (double) count / capacity : 0.0;

				FRPeriod period = new FRPeriod(start, end, false);
				FRRoom mRoom = new FRRoom(doorCode, uid);
				mRoom.setCapacity(capacity);
				
				// if this is the first iteration
				if (currentUID == null) {					
					currentUID = uid;
					currentDoorCode = mRoom.getDoorCode();
					currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, onlyFreeRooms);
				}

				// we move on to the next room thus re-initialize attributes
				// for the loop, as well as storing the previous room in the
				// result hashmap
				if (!uid.equals(currentUID)) {
					Occupancy mOccupancy = currentOccupancy.getOccupancy();

					addToHashMapOccupancy(currentDoorCode, mOccupancy, result);

					// remove the room from the list
					uidList.remove(currentUID);

					// re-initialize the value, and continue the process for
					// other rooms
					currentDoorCode = mRoom.getDoorCode();
					currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, onlyFreeRooms);
					currentUID = uid;
				}

				ActualOccupation accOcc = new ActualOccupation(period, available);
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

			// TODO refactor method to add in hashmap if not already present ..
			// for all the others rooms that hasn't been matched in the query,
			// we need to add them too

			if (!uidList.isEmpty()) {
				roomsListQueryFormat = "";
				for (i = 0; i < uidList.size() - 1; ++i) {
					roomsListQueryFormat += "?,";
				}

				roomsListQueryFormat += "?";
				String infoRequest = "SELECT rl.uid, rl.doorCode, rl.capacity "
						+ "FROM `fr-roomslist` rl " + "WHERE rl.uid IN("
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
					FRRoom mRoom = new FRRoom(doorCode, uid);
					mRoom.setCapacity(capacity);
					
					currentOccupancy = new OccupancySorted(mRoom, tsStart, tsEnd, onlyFreeRooms);
					FRPeriod period = new FRPeriod(tsStart, tsEnd, false);
					ActualOccupation accOcc = new ActualOccupation(period, true);
					accOcc.setProbableOccupation(0);
					currentOccupancy.addActualOccupation(accOcc);
					
					Occupancy mOccupancy = currentOccupancy.getOccupancy();
					addToHashMapOccupancy(mRoom.getDoorCode(), mOccupancy, result);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

//	/**
//	 * This method's job is create additional actualoccupation if needed to have
//	 * contiguous timestamps during the period specified by [tsStart; tsEnd]. By
//	 * default these additional actualoccupation are available with probable
//	 * occupation 0. It is required to have ordered timestamps by natural clock.
//	 */
//	private Occupancy fillGapsInOccupancy(long timestampStart,
//			long timestampEnd, List<ActualOccupation> accOcc, FRRoom room,
//			double worstRatio) {
//
//		long tsPerRoom = timestampStart;
//		Occupancy mOccupancy = new Occupancy();
//		mOccupancy.setRoom(room);
//		boolean isAtLeastFreeOnce = true;
//		boolean isAtLeastOccupiedOnce = false;
//		for (ActualOccupation actual : accOcc) {
//
//			long tsStart = Math.max(tsPerRoom, actual.getPeriod()
//					.getTimeStampStart());
//			long tsEnd = Math.min(timestampEnd, actual.getPeriod()
//					.getTimeStampEnd());
//
//			if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
//				// We got a free period of time !
//				// long nbHours = (long) (tsStart - tsPerRoom) / ONE_HOUR_MS;
//				List<ActualOccupation> subDivised = cutInStepActualOccupation(
//						tsPerRoom, tsStart, 0.0, 0, true);
//				addToOccupancy(mOccupancy, subDivised);
//
//				isAtLeastFreeOnce = true;
//			}
//
//			// if (actual.isAvailable()) {
//			// // this is a user occupation, need to subdivise it into steps
//			// FRPeriod period = actual.getPeriod();
//			// long periodStart = period.getTimeStampStart();
//			// long periodEnd = period.getTimeStampEnd();
//			//
//			// long nbHours = (long) (periodEnd - periodStart) / ONE_HOUR_MS;
//			// List<ActualOccupation> subDivised = cutInStepActualOccupation(
//			// periodStart, actual.getRatioOccupation(),
//			// actual.getProbableOccupation(), nbHours, true);
//			//
//			// addToOccupancy(mOccupancy, subDivised);
//			// } else {
//			// // otherwise simply add it because this is a room occupation and
//			// // there is no need for subdivision (user cannot specify he's
//			// // working there if there is a room occupancy)
//			// mOccupancy.addToOccupancy(actual);
//			// isAtLeastOccupiedOnce = true;
//			// }
//
//			long periodStart = actual.getPeriod().getTimeStampStart();
//			long periodEnd = actual.getPeriod().getTimeStampEnd();
//			if (periodEnd - periodStart > MIN_PERIOD) {
//				mOccupancy.addToOccupancy(actual);
//			}
//			tsPerRoom = tsEnd;
//
//		}
//
//		// There is some free time left after the last result
//		if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
//			long periodStart = tsPerRoom;
//			long periodEnd = timestampEnd;
//
//			// TODO problem if less than an hour
//			long nbHours = (long) ((periodEnd - periodStart) / ONE_HOUR_MS);
//			List<ActualOccupation> subDivised = cutInStepActualOccupation(
//					periodStart, periodEnd, 0.0, 0, true);
//
//			addToOccupancy(mOccupancy, subDivised);
//			// check if there is no time between the last occupancy and the end
//			// of the period (period has been subdivised in STEPS (if one hour,
//			// there might some minutes left))
//
//			long lastEnd = subDivised.get(subDivised.size() - 1).getPeriod()
//					.getTimeStampEnd();
//			if (timestampEnd - lastEnd > MARGIN_ERROR_TIMESTAMP) {
//				ActualOccupation mAccOcc = new ActualOccupation(new FRPeriod(
//						lastEnd + 1, timestampEnd, false), true);
//				mAccOcc.setProbableOccupation(0);
//				mAccOcc.setRatioOccupation(0.0);
//				mOccupancy.addToOccupancy(mAccOcc);
//			}
//
//			isAtLeastFreeOnce = true;
//		}
//
//		mOccupancy.setIsAtLeastFreeOnce(isAtLeastFreeOnce);
//		mOccupancy.setIsAtLeastOccupiedOnce(isAtLeastOccupiedOnce);
//		mOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);
//
//		return mOccupancy;
//	}

	private void addToOccupancy(Occupancy mOccupancy,
			List<ActualOccupation> accOcc) {
		List<ActualOccupation> alreadyFilled = mOccupancy.getOccupancy();
		if (alreadyFilled == null) {
			alreadyFilled = new ArrayList<ActualOccupation>();
		}

		alreadyFilled.addAll(accOcc);
		mOccupancy.setOccupancy(alreadyFilled);
	}

	/**
	 * Cut the period given (specified with the start and number of steps) in
	 * steps. All steps have same ratio and count. TODO passes parameters that
	 * are rounded to the hour ? actually if start is 10h30 it will go by 11h30,
	 * 12h30..
	 */
	private List<ActualOccupation> cutInStepActualOccupation(long tsStart,
			long tsEnd, double ratio, int count, boolean available) {
		ArrayList<ActualOccupation> cutted = new ArrayList<ActualOccupation>();

		// if this is an user occupation we need to insert it step by
		// step
		long timeToCompleteHour = ONE_HOUR_MS - (tsStart % ONE_HOUR_MS);
		long startInsert = tsStart + 1;
		long endFirstInsert = tsStart + timeToCompleteHour - 1;

		if (timeToCompleteHour > MARGIN_ERROR_TIMESTAMP
				&& timeToCompleteHour != ONE_HOUR_MS) {
			ActualOccupation mAccOcc = new ActualOccupation(new FRPeriod(
					startInsert, endFirstInsert, false), available);
			mAccOcc.setProbableOccupation(count);
			mAccOcc.setRatioOccupation(ratio);
			cutted.add(mAccOcc);
		}

		if (timeToCompleteHour == ONE_HOUR_MS) {
			timeToCompleteHour = 0;
		}

		// TODO optimization, don't compute i*ONE HOUR ... each time,
		// maybe better use addtion and keeping previous value each time
		long nextStart = tsStart + timeToCompleteHour;
		long timeAtEndInMin = tsEnd % ONE_HOUR_MS;
		long nbSteps = (long) (tsEnd - nextStart) / ONE_HOUR_MS;

		for (int i = 0; i < nbSteps; ++i) {
			ActualOccupation mAccOcc = new ActualOccupation(new FRPeriod(
					nextStart + i * ONE_HOUR_MS, nextStart + (i + 1)
							* ONE_HOUR_MS - 1, false), available);
			mAccOcc.setProbableOccupation(count);
			mAccOcc.setRatioOccupation(ratio);
			cutted.add(mAccOcc);
		}

		if (timeAtEndInMin > MARGIN_ERROR_TIMESTAMP) {
			ActualOccupation mAccOcc = new ActualOccupation(new FRPeriod(
					nextStart + nbSteps * ONE_HOUR_MS, nextStart + nbSteps
							* ONE_HOUR_MS + timeAtEndInMin, false), available);
			mAccOcc.setProbableOccupation(count);
			mAccOcc.setRatioOccupation(ratio);
			cutted.add(mAccOcc);
		}

		// for (int i = 0; i < nbStep; ++i) {
		// ActualOccupation accOcc = new ActualOccupation(new FRPeriod(tsStart
		// + ONE_HOUR_MS * i, tsStart + ONE_HOUR_MS * (i + 1),
		// available), available);
		// accOcc.setProbableOccupation(count);
		// accOcc.setRatioOccupation(ratio);
		// cutted.add(accOcc);
		// }

		return cutted;
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

	@Override
	public ImWorkingReply indicateImWorking(ImWorkingRequest request)
			throws TException {
		WorkingOccupancy work = request.getWork();
		FRPeriod period = work.getPeriod();
		FRRoom room = work.getRoom();
		boolean success = insertOccupancy(period, OCCUPANCY_TYPE.USER, room);
		if (success) {
			return new ImWorkingReply(HttpURLConnection.HTTP_OK, "");
		} else {
			return new ImWorkingReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					"Cannot insert user occupancy");
		}
	}

	@Override
	public WhoIsWorkingReply whoIsWorking(WhoIsWorkingRequest request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
