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
					+ "JOIN `fr-usersoccupancy` uo "
					+ "ON uo.uid = rl.uid "
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
				query.setLong(1, tsEnd);
				query.setLong(2, tsStart);
				query.setLong(3, tsEnd);
				query.setLong(4, tsStart);
				query.setLong(5, tsStart);
				query.setLong(6, tsEnd);

				ResultSet resultQuery = query.executeQuery();
				String currentRoom = null;
				String currentDoorCode = null;
				Occupancy currentOccupancy = new Occupancy();
				double worstRatio = 0.0;
				
				while (resultQuery.next()) {
					// extract attributes of record
					long start = resultQuery.getLong("timestampStart");
					long end = resultQuery.getLong("timestampEnd");
					String uid = resultQuery.getString("uid");
					String doorCode = resultQuery.getString("doorCode");
					int count = resultQuery.getInt("count");
					int capacity = resultQuery.getInt("capacity");

					FRPeriod period = new FRPeriod(start, end, false);
					FRRoom room = new FRRoom(doorCode, uid);

					if (currentRoom == null) {
						currentRoom = uid;
						currentDoorCode = doorCode;
						currentOccupancy.setRoom(room);
						currentOccupancy.setIsAtLeastFreeOnce(true);
						currentOccupancy.setIsAtLeastOccupiedOnce(false);
					}

					if (!uid.equals(currentRoom)) {
						currentOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);
						worstRatio = 0.0;
						
						//extract building, insert it into the hashmap
						String building = Utils.extractBuilding(currentDoorCode);
						List<Occupancy> occ = result.get(building);
						
						if (occ  == null) {
							occ = new ArrayList<Occupancy>();
							result.put(building, occ);
						}
						occ.add(currentOccupancy);
						
						//re-initialize the value, and continue the process for other rooms
						currentRoom = uid;
						currentDoorCode = doorCode;
						currentOccupancy = new Occupancy();
						currentOccupancy.setRoom(room);
						currentOccupancy.setIsAtLeastFreeOnce(true);
						currentOccupancy.setIsAtLeastOccupiedOnce(false);
					}

					long nbHours = (start - end) % ONE_HOUR_MS;
					double ratio = capacity > 0 ? (double) (count / capacity) : 0.0;
					
					if (ratio > worstRatio) {
						worstRatio = ratio;
					}
					
					if (nbHours <= 1) {
						ActualOccupation accOcc = new ActualOccupation(period,
								true);
						accOcc.setProbableOccupation(count);
						accOcc.setRatioOccupation(ratio);
						currentOccupancy.addToOccupancy(accOcc);
					} else {
						List<ActualOccupation> accOcc = cutInStepActualOccupation(
								start, ratio, count,  nbHours, true);
						List<ActualOccupation> actual = currentOccupancy.getOccupancy();
						actual.addAll(accOcc);
						currentOccupancy.setOccupancy(actual);
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	private List<ActualOccupation> cutInStepActualOccupation(long tsStart,
			double ratio, int count, long nbStep, boolean available) {
		ArrayList<ActualOccupation> cutted = new ArrayList<ActualOccupation>();

		for (int i = 0; i < nbStep; ++i) {
			ActualOccupation accOcc = new ActualOccupation(new FRPeriod(tsStart
					+ ONE_HOUR_MS * i, tsStart + ONE_HOUR_MS * (i + 1), false),
					available);
			accOcc.setProbableOccupation(count);
			accOcc.setRatioOccupation(ratio);
			cutted.add(accOcc);
		}

		return cutted;
	}

	private HashMap<String, List<Occupancy>> getOccupancyOfSpecificRoom(
			List<String> uidList, boolean onlyFreeRooms, long tsStart,
			long tsEnd) {

		if (uidList.isEmpty()) {
			return getOccupancyOfAnyFreeRoom(onlyFreeRooms, tsStart, tsEnd);
		}

		int numberOfRooms = uidList.size();
		// formatting for the query
		String roomsListQueryFormat = "";
		for (int i = 0; i < numberOfRooms - 1; ++i) {
			roomsListQueryFormat += "?,";
		}
		roomsListQueryFormat += "?";
		// TODO join should also take into account user occupancy that start or
		// end after the period but have at least one timestamp in the interval
		String request = "SELECT ro.timestampStart, ro.timestampEnd, "
				+ "rl.doorCode, rl.uid, uo.count, rl.capacity, "
				+ "uo.timestampStart AS userStart, uo.timestampEnd AS userEnd "
				+ "FROM `fr-roomsoccupancy` ro, `fr-roomslist` rl "
				+ "LEFT OUTER JOIN `fr-usersoccupancy` uo "
				+ "ON uo.uid = rl.uid "
				+ "AND ((uo.timestampEnd <= ? AND uo.timestampEnd >= ? ) "
				+ "OR (uo.timestampStart <= ? AND uo.timestampStart >= ?)"
				+ "OR (uo.timestampStart <= ? AND uo.timestampEnd >= ?)) "
				+ "WHERE rl.uid IN (" + roomsListQueryFormat
				+ ") AND ro.uid = rl.uid AND "
				+ "((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
				+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
				+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?)) "
				+ "ORDER BY rl.uid ASC";

		Connection connectBDD;
		try {
			connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD.prepareStatement(request);
			query.setLong(1, tsEnd);
			query.setLong(2, tsStart);
			query.setLong(3, tsEnd);
			query.setLong(4, tsStart);
			query.setLong(5, tsStart);
			query.setLong(6, tsEnd);

			for (int i = 7; i < numberOfRooms + 7; ++i) {
				query.setString(i, uidList.get(i - 7));
			}

			query.setLong(numberOfRooms + 7, tsEnd);
			query.setLong(numberOfRooms + 8, tsStart);
			query.setLong(numberOfRooms + 9, tsEnd);
			query.setLong(numberOfRooms + 10, tsStart);
			query.setLong(numberOfRooms + 11, tsStart);
			query.setLong(numberOfRooms + 12, tsEnd);

			ResultSet result = query.executeQuery();

			while (result.next()) {
				System.out.println(result.getString("doorCode") + " from "
						+ result.getLong("timestampStart") + " to "
						+ result.getLong("timestampEnd") + " : user from "
						+ result.getString("userStart") + " to "
						+ result.getString("userEnd"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
