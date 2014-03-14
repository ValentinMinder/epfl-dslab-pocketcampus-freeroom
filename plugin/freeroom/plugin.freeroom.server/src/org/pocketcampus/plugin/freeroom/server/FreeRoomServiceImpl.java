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

/**
 * The actual implementation of the server side of the FreeRoom Plugin.
 * 
 * It responds to different types of request from the clients.
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
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

		FreeRoomReply reply = new FreeRoomReply(HttpURLConnection.HTTP_CREATED,
				"" + HttpURLConnection.HTTP_CREATED);

		// The client issue a request, convertMinPrecision's job is to adapt the
		// period. See its doc for more information.
		request = Utils.convertMinPrecision(request);
		FRPeriod period = request.getPeriod();
		long ts_start = period.getTimeStampStart();
		long ts_end = period.getTimeStampEnd();
		boolean recurrent = period.isRecurrent();

		if (!recurrent) {
			Set<FRRoom> rooms = getFreeRoom(ts_start, ts_end);
			if (rooms != null) {
				reply = new FreeRoomReply(HttpURLConnection.HTTP_OK, ""
						+ HttpURLConnection.HTTP_OK);
				reply.setRooms(rooms);
				reply.setRoomsIsSet(true);
			} else {
				// TODO: how to differenciate server error or bad request ?
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
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(start);
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(end);

		// if (startDate.compareTo(endDate) <= 0) {
		// throw new TException("Start date must be before end date");
		// }

		// depends from the structure of database, need to change probably!
		// doesn't support overnight searches, only MON-SUN 8am-7pm
		int day = startDate.get(Calendar.DAY_OF_WEEK);
		int starthour = startDate.get(Calendar.HOUR_OF_DAY);
		int endhour = endDate.get(Calendar.HOUR_OF_DAY);
		// according to java.Calendar, Monday = 2 !!!
		System.out.println("Day: " + day + "/ from hour " + starthour
				+ "/ to hour" + endhour);

		// All this was copied from previous method!
		// TODO: validate for EVERY hour;
		if (starthour < 8 || endhour > 19) {
			throw new TException("unsupported timestamps: outside boundaries");
		}
		if (starthour >= endhour) {
			// TODO: change exception or handling
			// throw new TException("unsupported timestamps: same timestamps");
		}

		HashSet<FRRoom> freerooms = new HashSet<FRRoom>();
		try {
			Connection connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD
					.prepareStatement("SELECT rl.building, rl.room_number "
							+ "FROM roomslist rl "
							+ "WHERE rl.rid NOT IN "
							+ "(SELECT ro.rid FROM roomsoccupancy ro "
							+ "WHERE ((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
							+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)) )");

			// filling the query with values
			query.setLong(1, end);
			query.setLong(2, start);
			query.setLong(3, end);
			query.setLong(4, start);

			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String building = resultQuery.getString("building");
				int room_number = resultQuery.getInt("room_number");
				FRRoom r = new FRRoom();
				r.setBuilding(building);
				r.setNumber(room_number + "");
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
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<FRRoom> rooms = request.getListFRRoom();
		FRPeriod period = request.getPeriod();
		long timestampStart = period.getTimeStampStart();
		long timestampEnd = period.getTimeStampEnd();

		System.out.println((timestampStart - timestampEnd) / (3600 * 1000));

		ArrayList<Occupancy> occupancies = new ArrayList<Occupancy>();

		try {
			Connection connectBDD = connMgr.getConnection();

			for (FRRoom room : rooms) {
				PreparedStatement query = connectBDD
						.prepareStatement("SELECT ro.timestampStart, ro.timestampEnd "
								+ "FROM roomsoccupancy ro, roomslist rl "
								+ "WHERE rl.building = ? AND rl.room_number = ? "
								+ "AND ro.rid = rl.rid AND "
								+ "((ro.timestampEnd <= ? AND ro.timestampEnd >= ? ) "
								+ "OR (ro.timestampStart <= ? AND ro.timestampStart >= ?)"
								+ "OR (ro.timestampStart <= ? AND ro.timestampEnd >= ?)) "
								+ "ORDER BY ro.timestampStart ASC");
				query.setString(1, room.getBuilding());
				query.setString(2, room.getNumber());
				query.setLong(3, timestampEnd);
				query.setLong(4, timestampStart);
				query.setLong(5, timestampEnd);
				query.setLong(6, timestampStart);
				query.setLong(7, timestampStart);
				query.setLong(8, timestampEnd);

				// filling the query with values

				ResultSet resultQuery = query.executeQuery();
				Occupancy mOccupancy = new Occupancy();
				System.out.println("room:" + room);
				mOccupancy.setRoom(room);

				// timestamp used to generate the occupations accross the
				// FRPeriod
				long tsPerRoom = timestampStart;
				while (resultQuery.next()) {
					long tsStart = resultQuery.getLong("timestampStart");
					long tsEnd = resultQuery.getLong("timestampEnd");

					if (tsStart - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
						// We got a free period of time !
						ActualOccupation mOcc = new ActualOccupation();
						mOcc.setPeriod(new FRPeriod(tsPerRoom, tsStart - 1,
								false));
						mOcc.setAvailable(true);
						mOcc.setOccupationType(OccupationType.FREE);
						mOccupancy.addToOccupancy(mOcc);
					}

					ActualOccupation mAccOcc = new ActualOccupation();
					// TODO reminder that recurrent is set to false for now, but
					// it can evolve in the future
					mAccOcc.setPeriod(new FRPeriod(tsStart, tsEnd, false));
					mAccOcc.setAvailable(false);
					// TODO reminder default value ISA
					mAccOcc.setOccupationType(OccupationType.ISA);
					mOccupancy.addToOccupancy(mAccOcc);
					tsPerRoom = tsEnd;

				}

				// There is some free time left after the last result
				if (timestampEnd - tsPerRoom > MARGIN_ERROR_TIMESTAMP) {
					ActualOccupation mOcc = new ActualOccupation();
					mOcc.setPeriod(new FRPeriod(tsPerRoom, timestampEnd, false));
					mOcc.setAvailable(true);
					mOcc.setOccupationType(OccupationType.FREE);
					mOccupancy.addToOccupancy(mOcc);
				}
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
		String txt = request.getConstraint();
		// avoid all whitespaces for requests
		// TODO: be resistent to empty queries!
		txt = txt.trim();
		txt = txt.replaceAll("\\s", "");
		try {
			Connection connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD.prepareStatement("SELECT * "
					+ "FROM roomslist rl "
					+ "WHERE CONCAT(rl.building, rl.room_number) LIKE (?) "
					// TODO: verify the order for CO 1 and CO 123 ...
					+ "ORDER BY CONCAT(rl.building, rl.room_number) ASC");
			query.setString(1, txt + "%");

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
