package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomType;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.OccupationType;
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
	private ConnectionManager connMgr;
	// margin for error is a minute
	private final long MARGIN_ERROR_TIMESTAMP = 60 * 1000;

	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ...");
		try {
			connMgr = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL")
					+ "test", PC_SRV_CONFIG.getString("DB_USERNAME"),
					PC_SRV_CONFIG.getString("DB_PASSWORD"));

		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	// for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
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
						"could be 400 or 500");
			}
			return reply;
		} else {
			// TODO: support recurrent request
			reply = new FreeRoomReply(HttpURLConnection.HTTP_INTERNAL_ERROR,
					"reccurent request not supported yet");
			return reply;
		}
	}

	private Set<FRRoom> getFreeRoom(long start, long end) throws TException {

		HashSet<FRRoom> freerooms = new HashSet<FRRoom>();
		try {
			Connection connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD
					.prepareStatement("SELECT rl.doorCode, rl.uid "
							+ "FROM fr-roomslist rl "
							+ "WHERE rl.uid NOT IN "
							+ "(SELECT ro.uid FROM fr-roomsoccupancy ro "
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
				PreparedStatement query = connectBDD
						.prepareStatement("SELECT ro.timestampStart, ro.timestampEnd, " +
								"rl.doorCode "
								+ "FROM fr-roomsoccupancy ro, fr-roomslist rl "
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
				Occupancy mOccupancy = new Occupancy();

				boolean isAtLeastOccupiedOnce = false;
				boolean isAtLeastFreeOnce = false;
				FRRoom room = null;
				// timestamp used to generate the occupations accross the
				// FRPeriod
				long tsPerRoom = timestampStart;
				while (resultQuery.next()) {
					if (room == null) {
						room = new FRRoom(resultQuery.getString("doorCode"),
								mUid);
						mOccupancy.setRoom(room);
					}

					long tsStart = Math.max(tsPerRoom,
							resultQuery.getLong("timestampStart"));
					long tsEnd = Math.min(timestampEnd,
							resultQuery.getLong("timestampEnd"));

					if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
						// We got a free period of time !
						ActualOccupation mOcc = new ActualOccupation();
						mOcc.setPeriod(new FRPeriod(tsPerRoom, tsStart - 1,
								false));
						mOcc.setAvailable(true);
						mOcc.setOccupationType(OccupationType.FREE);
						mOccupancy.addToOccupancy(mOcc);
						isAtLeastFreeOnce = true;
					}

					ActualOccupation mAccOcc = new ActualOccupation();
					// TODO reminder that recurrent is set to false for now,
					// but
					// it can evolve in the future
					mAccOcc.setPeriod(new FRPeriod(tsStart, tsEnd, false));
					mAccOcc.setAvailable(false);
					// TODO reminder default value ISA
					mAccOcc.setOccupationType(OccupationType.ISA);
					mOccupancy.addToOccupancy(mAccOcc);
					isAtLeastOccupiedOnce = true;

					tsPerRoom = tsEnd;

				}

				// There is some free time left after the last result
				if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
					ActualOccupation mOcc = new ActualOccupation();
					mOcc.setPeriod(new FRPeriod(tsPerRoom, timestampEnd, false));
					mOcc.setAvailable(true);
					mOcc.setOccupationType(OccupationType.FREE);
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

	@Override
	public AutoCompleteReply autoCompleteRoom(AutoCompleteRequest request)
			throws TException {
		// TODO this method wont work with full "door code" like PH D2 398
		// TODO dont work with UID at the moment

		AutoCompleteReply reply = new AutoCompleteReply(
				HttpURLConnection.HTTP_CREATED, ""
						+ HttpURLConnection.HTTP_CREATED);

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
		String txt = request.getConstraint();
		// avoid all whitespaces for requests
		// TODO: be resistent to empty queries!
		txt = txt.trim();
		txt = txt.replaceAll("\\s", "");
		try {
			Connection connectBDD = connMgr.getConnection();
			String requestSQL = "";
			if (forbiddenRooms == null) {
				requestSQL = "SELECT * " + "FROM fr-roomslist rl "
						+ "WHERE rl.uid LIKE (?) "
						+ "ORDER BY rl.doorCode ASC";
			} else {
				requestSQL = "SELECT * " + "FROM fr-roomslist rl "
						+ "WHERE rl.uid LIKE (?) "
						+ "AND rl.uid NOT IN ("
						+ forbidRoomsSQL + ") "
						// TODO: verify the order for CO 1 and CO 123 ...
						+ "ORDER BY rl.doorCode ASC";
			}

			PreparedStatement query = connectBDD.prepareStatement(requestSQL);
			query.setString(1, txt + "%");

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
				String building = resultQuery.getString("building");
				int number = resultQuery.getInt("room_number");
				FRRoom frRoom = new FRRoom(building, number + "");
				String type = resultQuery.getString("type");
				if (type != null) {
					try {
						FRRoomType t = FRRoomType.valueOf(type);
						frRoom.setType(t);
					} catch (IllegalArgumentException e) {
						System.err.println("Type not known " + type);
						e.printStackTrace();
					}
				}
				int cap = resultQuery.getInt("capacity");
				if (cap > 0) {
					frRoom.setCapacity(cap);
				}
				rooms.add(frRoom);

			}
			reply = new AutoCompleteReply(HttpURLConnection.HTTP_OK, ""
					+ HttpURLConnection.HTTP_OK);
			reply.setListFRRoom(rooms);

		} catch (SQLException e) {
			reply = new AutoCompleteReply(
					HttpURLConnection.HTTP_INTERNAL_ERROR, ""
							+ HttpURLConnection.HTTP_INTERNAL_ERROR);
			e.printStackTrace();
		}
		return reply;
	}
}
