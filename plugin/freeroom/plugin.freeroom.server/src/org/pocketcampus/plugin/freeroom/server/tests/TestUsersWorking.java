package org.pocketcampus.plugin.freeroom.server.tests;

import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.shared.FRCourse;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

public class TestUsersWorking {
	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DBMS_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	final static String DB_URL = "jdbc:mysql://localhost/pocketcampus?allowMultiQueries=true";

	private static ConnectionManager conn;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			conn = new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This DOES no "test", it's only a way to see that it works so far!
	 */
	@Test
	public void testbasicFunction() {
		FreeRoomServiceImpl f = new FreeRoomServiceImpl(conn);
		// TODO: uncomment this the very first time!
		// f.initUserOccupation();

		FRRoom mFrRoom = new FRRoom("CO 6", "10698");

		FRPeriod period = new FRPeriod(System.currentTimeMillis()
				+ FRTimes.ONE_HOUR_IN_MS, System.currentTimeMillis() + 3
				* FRTimes.ONE_HOUR_IN_MS, false);
		WorkingOccupancy work = new WorkingOccupancy(period, mFrRoom);
		work.setMessage("HW4 - deadline 2013/12/03");
		FRCourse course = new FRCourse("CS-306", "Sweng");
		ImWorkingRequest request = new ImWorkingRequest(work);
		work.setCourse(course);
		try {
			f.indicateImWorking(request);
		} catch (TException e) {
			e.printStackTrace();
		}
		WhoIsWorkingRequest req = new WhoIsWorkingRequest(period);
		try {
			WhoIsWorkingReply reply = f.whoIsWorking(req);
			List<WorkingOccupancy> list = reply.getTheyAreWorking();
			for (WorkingOccupancy workingOccupancy : list) {
				System.out.println(workingOccupancy);
			}
		} catch (TException e) {
			e.printStackTrace();
		}

		FRPeriod newperiod = new FRPeriod(System.currentTimeMillis()
				+ FRTimes.ONE_HOUR_IN_MS, System.currentTimeMillis() + 3
				* FRTimes.ONE_HOUR_IN_MS, false);

		List<Integer> list = f.getUserOccupancy(newperiod, mFrRoom);
		List<FRPeriod> listPeriod = FRTimes.getFRPeriodByStep(newperiod);
		for (int i = 0; i < list.size(); i++) {
			FRPeriod period2 = listPeriod.get(i);
			System.out.println("From " + new Date(period2.getTimeStampStart())
					+ " to " + new Date(period2.getTimeStampEnd()) + ": "
					+ list.get(i) + " probable occupancies.");
		}
	}
}
