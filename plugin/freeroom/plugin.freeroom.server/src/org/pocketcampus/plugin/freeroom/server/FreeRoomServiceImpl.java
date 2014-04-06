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
import org.bouncycastle.jce.provider.symmetric.Skipjack.MacCFB8;
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

import android.location.GpsStatus.NmeaListener;

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

	// margin for error is a minute
	private final long MARGIN_ERROR_TIMESTAMP = 60 * 1000;
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
		boolean updateEWA = true;
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
	 * Whenever you need to insert an occupancy you should call this one.
	 * 
	 * @param period
	 * @param type
	 * @param room
	 * @return
	 */
	public boolean insertOccupancy(FRPeriod period, OCCUPANCY_TYPE type,
			FRRoom room) {
		return insertAndCheckOccupancyRoom(period, room, type);
	}

	private boolean insertAndCheckOccupancyRoom(FRPeriod period, FRRoom room,
			OCCUPANCY_TYPE typeToInsert) {
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

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
					return false;
				} else if (typeToInsert == OCCUPANCY_TYPE.ROOM) {
					// else, we need to adapt the boundaries of the overlapping
					// entries, rooms occupancy has the priority over user
					// occupancy

					if (start > tsStart && start < tsEnd) {
						adaptTimeStampOccupancy(uid, start, tsEnd + 1, end);
					} else if (end < tsEnd && end > start) {
						adaptTimeStampOccupancy(uid, start, start, tsStart - 1);
					} else {
						// simply delete it if it is entirely in the period
						deleteOccupancy(uid, start);
					}
				} else if (typeToInsert == OCCUPANCY_TYPE.USER
						&& type == OCCUPANCY_TYPE.ROOM) {
					// simply adapt our boundaries
					if (start > tsStart && start < tsEnd) {
						tsEnd = start - 1;
					} else if (end < tsEnd && end > start) {
						tsStart = end + 1;
					} else {
						// there is a course, no possiblity to insert a user
						// occupancy here
						return false;
					}
				} else {
					if (start > tsStart && start < tsEnd) {
						// shouldn't happen
						System.out
								.println("Error while inserting, trying to insert user occupancy that PARTIALLY overlap another useroccupancy");
						return false;
					} else if (end < tsEnd && end > start) {
						// shouldn't happen
						System.out
								.println("Error while inserting, trying to insert user occupancy that PARTIALLY overlap another useroccupancy");

						return false;
					}
					// otherwise no problem, insertion is step by step
				}
			}

			// and now insert it !
			String insertRequest = "INSERT INTO `fr-occupancy` (uid, timestampStart, timestampEnd, type, count) "
					+ "VALUES (?, ?, ?, ?, ?)";

			PreparedStatement insertQuery = connectBDD
					.prepareStatement(insertRequest);

			insertQuery.setString(1, room.getUid());
			insertQuery.setLong(2, tsStart);
			insertQuery.setLong(3, tsEnd);
			insertQuery.setString(4, OCCUPANCY_TYPE.ROOM.toString());
			insertQuery.setInt(5, 0);

			return insertQuery.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void adaptTimeStampOccupancy(String uid, long tsStart,
			long newStart, long newEnd) {
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
			occupancies = getOccupancyOfSpecificRoom(uidList, onlyFreeRoom,
					period.getTimeStampStart(), period.getTimeStampEnd());
		}

		reply.setOccupancyOfRooms(occupancies);
		return reply;
	}

	// TODO add verification that timestamps received do not go beyong the
	// desired period and if so simply rescale it
	// TODO fill gaps (if user occupancy is from 10 to 11 and total period is 10
	// 12, need to add 11 12
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

				// and also select user occupancy of theses rooms
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

				String currentRoom = null;
				String currentDoorCode = null;
				Occupancy currentOccupancy = new Occupancy();
				int currentCapacity = 0;
				double worstRatio = 0.0;

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
					if (currentRoom == null) {
						FRRoom mRoom = rooms.get(uid);
						currentRoom = uid;
						currentDoorCode = mRoom.getDoorCode();
						currentOccupancy.setRoom(mRoom);
						currentOccupancy.setIsAtLeastFreeOnce(true);
						currentOccupancy.setIsAtLeastOccupiedOnce(false);
						currentCapacity = mRoom.getCapacity();
					}

					// we move on to the next room thus re-initialize attributes
					// for the loop, as well as storing the previous room in the
					// result hashmap
					if (!uid.equals(currentRoom)) {
						currentOccupancy
								.setRatioWorstCaseProbableOccupancy(worstRatio);
						worstRatio = 0.0;

						// extract building, insert it into the hashmap
						String building = Utils
								.extractBuilding(currentDoorCode);
						List<Occupancy> occ = result.get(building);

						if (occ == null) {
							occ = new ArrayList<Occupancy>();
							result.put(building, occ);
						}
						occ.add(currentOccupancy);

						// re-initialize the value, and continue the process for
						// other rooms
						FRRoom mRoom = rooms.get(uid);
						currentDoorCode = mRoom.getDoorCode();
						currentOccupancy = new Occupancy();
						currentOccupancy.setRoom(mRoom);
						currentOccupancy.setIsAtLeastFreeOnce(true);
						currentOccupancy.setIsAtLeastOccupiedOnce(false);
						currentCapacity = mRoom.getCapacity();

						// remove the room from the list
						rooms.remove(currentRoom);
						currentRoom = uid;
					}

					long nbHours = (start - end) % ONE_HOUR_MS;
					double ratio = currentCapacity > 0 ? (double) (count / currentCapacity)
							: 0.0;

					if (ratio > worstRatio) {
						worstRatio = ratio;
					}

					// we subdivise each freeroom by step of one hour (uses in
					// client-side)
					if (nbHours <= 1) {
						ActualOccupation accOcc = new ActualOccupation(period,
								true);
						accOcc.setProbableOccupation(count);
						accOcc.setRatioOccupation(ratio);
						currentOccupancy.addToOccupancy(accOcc);
					} else {
						List<ActualOccupation> accOcc = cutInStepActualOccupation(
								start, ratio, count, nbHours, true);
						List<ActualOccupation> actual = currentOccupancy
								.getOccupancy();
						actual.addAll(accOcc);
						currentOccupancy.setOccupancy(actual);
					}

				}

				// and finally, check if there is some free rooms left that have
				// no user occupancy and need manual action (i.e set ratio to 0
				// and has to be added in the result hashmap)

				for (FRRoom mRoom : rooms.values()) {
					List<ActualOccupation> accOcc = cutInStepActualOccupation(
							tsStart, 0.0, 0, (tsEnd - tsStart) % ONE_HOUR_MS,
							true);
					String building = Utils
							.extractBuilding(mRoom.getDoorCode());
					List<Occupancy> mOccupancies = result.get(building);

					if (mOccupancies == null) {
						mOccupancies = new ArrayList<Occupancy>();
						result.put(building, mOccupancies);
					}
					Occupancy currentOcc = new Occupancy(mRoom, accOcc, false,
							true);
					mOccupancies.add(currentOcc);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
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
			// TODO check for left outer join
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

			// similarly as for getOccupancyOfAnyFreeRoom we do it room by room
			// (query beeing sorted by room's uid and then by timestampStart)
			String currentUID = null;
			String currentDoorCode = null;
			double worstRatio = 0.0;
			List<ActualOccupation> actualOcc = null;
			FRRoom actualRoom = null;
			long startPeriod = tsStart;
			long endPeriod = tsEnd;

			// TODO if there is only one room need explicit action
			while (resultQuery.next()) {
				// extract attributes of record
				long start = resultQuery.getLong("timestampStart");
				long end = resultQuery.getLong("timestampEnd");
				String uid = resultQuery.getString("uid");
				String doorCode = resultQuery.getString("doorCode");
				int count = resultQuery.getInt("count");
				String type = resultQuery.getString("type");
				int capacity = resultQuery.getInt("capacity");
				double ratio = capacity > 0 ? (double) count / capacity : 0.0;
				boolean available = false;

				if (type != null
						&& OCCUPANCY_TYPE.valueOf(type) == OCCUPANCY_TYPE.USER) {
					available = true;
				}

				FRPeriod period = new FRPeriod(start, end, false);

				// if this is the first iteration
				if (currentUID == null) {
					actualRoom = new FRRoom(doorCode, uid);
					actualRoom.setCapacity(capacity);

					currentUID = uid;
					currentDoorCode = doorCode;
					actualOcc = new ArrayList<ActualOccupation>();
				}

				System.out.println("handling " + currentUID);
				// we move on to the next room thus re-initialize attributes
				// for the loop, as well as storing the previous room in the
				// result hashmap
				if (!uid.equals(currentUID)) {
					uidList.remove(currentUID);

					Occupancy currentOccupancy = fillGapsInOccupancy(
							startPeriod, endPeriod, actualOcc, actualRoom,
							worstRatio);

					// extract building, insert it into the hashmap
					String building = Utils.extractBuilding(currentDoorCode);
					List<Occupancy> occ = result.get(building);

					if (occ == null) {
						occ = new ArrayList<Occupancy>();
						result.put(building, occ);
					}
					occ.add(currentOccupancy);

					// re-initialize the value, and continue the process for
					// other rooms
					currentDoorCode = doorCode;
					currentUID = uid;
					startPeriod = start;
					actualRoom = new FRRoom(currentDoorCode, currentUID);
					actualRoom.setCapacity(capacity);
					actualOcc = new ArrayList<ActualOccupation>();
					worstRatio = 0.0;
				}

				endPeriod = end;
				ActualOccupation mActOcc = new ActualOccupation(period,
						available);
				mActOcc.setProbableOccupation(count);
				mActOcc.setRatioOccupation(ratio);

				if (ratio > worstRatio) {
					worstRatio = ratio;
				}
				actualOcc.add(mActOcc);
			}

			// TODO check if remove of uidList actually works (also in the big
			// query above)
			if (!actualOcc.isEmpty()) {
				// the last room need to be added to the result hashmap
				// it is important to remove them once a room has been handled
				// due to the last part of the method (see below)
				uidList.remove(currentUID);
				Occupancy currentOccupancy = fillGapsInOccupancy(startPeriod,
						endPeriod, actualOcc, actualRoom, worstRatio);

				// extract building, insert it into the hashmap
				String building = Utils.extractBuilding(currentDoorCode);
				List<Occupancy> occ = result.get(building);

				if (occ == null) {
					occ = new ArrayList<Occupancy>();
				}
				occ.add(currentOccupancy);
				result.put(building, occ);
			}

			// TODO refactor method to add in hashmap if not already present ..
			// for all the others rooms that hasn't been matched in the query,
			// we need to add them too

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

				actualRoom = new FRRoom(doorCode, uid);
				actualRoom.setCapacity(capacity);
				Occupancy currentOccupancy = fillGapsInOccupancy(tsStart,
						tsEnd, new ArrayList<ActualOccupation>(), actualRoom,
						0.0);

				// extract building, insert it into the hashmap
				String building = Utils.extractBuilding(doorCode);
				List<Occupancy> occ = result.get(building);

				if (occ == null) {
					occ = new ArrayList<Occupancy>();
					result.put(building, occ);
				}
				occ.add(currentOccupancy);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method's job is create additional actualoccupation if needed to have
	 * contiguous timestamps during the period specified by [tsStart; tsEnd]. By
	 * default these additional actualoccupation are available with probable
	 * occupation 0. It is required to have ordered timestamps by natural clock.
	 */
	private Occupancy fillGapsInOccupancy(long timestampStart,
			long timestampEnd, List<ActualOccupation> accOcc, FRRoom room,
			double worstRatio) {

		long tsPerRoom = timestampStart;
		Occupancy mOccupancy = new Occupancy();
		mOccupancy.setRoom(room);
		boolean isAtLeastFreeOnce = true;
		boolean isAtLeastOccupiedOnce = false;
		for (ActualOccupation actual : accOcc) {

			long tsStart = Math.max(tsPerRoom, actual.getPeriod()
					.getTimeStampStart());
			long tsEnd = Math.min(timestampEnd, actual.getPeriod()
					.getTimeStampEnd());

			if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
				// We got a free period of time !
				ActualOccupation mOcc = new ActualOccupation();
				FRPeriod myPeriod = new FRPeriod(tsPerRoom, tsStart - 1, false);
				mOcc.setPeriod(myPeriod);
				mOcc.setAvailable(true);
				mOcc.setProbableOccupation(0);
				// TODO also cut in step here
				mOccupancy.addToOccupancy(mOcc);
				isAtLeastFreeOnce = true;
			}

			if (actual.isAvailable()) {
				// this is a user occupation, need to subdivise it into steps
				FRPeriod period = actual.getPeriod();
				long periodStart = period.getTimeStampStart();
				long periodEnd = period.getTimeStampEnd();

				long nbHours = (long) (periodEnd - periodStart) / ONE_HOUR_MS;
				List<ActualOccupation> subDivised = cutInStepActualOccupation(
						periodStart, actual.getRatioOccupation(),
						actual.getProbableOccupation(), nbHours, true);

				// add it
				List<ActualOccupation> alreadyFilled = mOccupancy
						.getOccupancy();
				alreadyFilled.addAll(subDivised);
				mOccupancy.setOccupancy(alreadyFilled);
			} else {
				// otherwise simply add it because this is a room occupation and
				// there is no need for subdivision (user cannot specify he's
				// working there if there is a room occupancy)
				mOccupancy.addToOccupancy(actual);
				isAtLeastOccupiedOnce = true;
			}

			tsPerRoom = tsEnd;

		}

		// There is some free time left after the last result
		if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
			long periodStart = tsPerRoom;
			long periodEnd = timestampEnd;

			// TODO problem is less than an hour
			long nbHours = (long) ((periodEnd - periodStart) / ONE_HOUR_MS);
			List<ActualOccupation> subDivised = cutInStepActualOccupation(
					periodStart, 0.0, 0, nbHours, true);

			// add it
			List<ActualOccupation> alreadyFilled = mOccupancy.getOccupancy();

			if (alreadyFilled == null) {
				alreadyFilled = new ArrayList<ActualOccupation>();
			}

			alreadyFilled.addAll(subDivised);
			// check if there is no time between the last occupancy and the end
			// of the period (period has been subdivised in STEPS (if one hour,
			// there might some minutes left))

			long lastEnd = periodStart + ONE_HOUR_MS* nbHours;
			if (timestampEnd - lastEnd > MARGIN_ERROR_TIMESTAMP) {
				ActualOccupation mAccOcc = new ActualOccupation(new FRPeriod(
						lastEnd + 1, timestampEnd, false), true);
				mAccOcc.setProbableOccupation(0);
				mAccOcc.setRatioOccupation(0.0);
				alreadyFilled.add(mAccOcc);
			}

			mOccupancy.setOccupancy(alreadyFilled);
			isAtLeastFreeOnce = true;
		}

		mOccupancy.setIsAtLeastFreeOnce(isAtLeastFreeOnce);
		mOccupancy.setIsAtLeastOccupiedOnce(isAtLeastOccupiedOnce);
		mOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);

		return mOccupancy;
	}

	/**
	 * Cut the period given (specified with the start and number of steps) in
	 * steps. All steps have same ratio and count. TODO passes parameters that
	 * are rounded to the hour ? actually if start is 10h30 it will go by 11h30,
	 * 12h30..
	 */
	private List<ActualOccupation> cutInStepActualOccupation(long tsStart,
			double ratio, int count, long nbStep, boolean available) {
		ArrayList<ActualOccupation> cutted = new ArrayList<ActualOccupation>();

		for (int i = 0; i < nbStep; ++i) {
			ActualOccupation accOcc = new ActualOccupation(new FRPeriod(tsStart
					+ ONE_HOUR_MS * i, tsStart + ONE_HOUR_MS * (i + 1),
					available), available);
			accOcc.setProbableOccupation(count);
			accOcc.setRatioOccupation(ratio);
			cutted.add(accOcc);
		}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WhoIsWorkingReply whoIsWorking(WhoIsWorkingRequest request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
