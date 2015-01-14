package org.pocketcampus.plugin.freeroom.server.exchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import microsoft.exchange.webservices.data.AttendeeAvailability;
import microsoft.exchange.webservices.data.AttendeeInfo;
import microsoft.exchange.webservices.data.AvailabilityData;
import microsoft.exchange.webservices.data.CalendarEvent;
import microsoft.exchange.webservices.data.EmailAddress;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.NameResolution;
import microsoft.exchange.webservices.data.NameResolutionCollection;
import microsoft.exchange.webservices.data.ResolveNameSearchLocation;
import microsoft.exchange.webservices.data.ServiceError;
import microsoft.exchange.webservices.data.TimeWindow;
import microsoft.exchange.webservices.data.WebCredentials;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class ExchangeEntry {
	private String gasparUserName = "";
	private String gasparPassword = "";
	private String emailAddress = "julien.weber@epfl.ch";
	private String domain = "intranet";

	private static String defaultDomain = "intranet";
	private static String propertyFile = "src" + File.separator
			+ "local.properties";
	private Properties properties = new Properties();

	private static URI EWAURI;
	// should be new URI("https://ewa.epfl.ch/EWS/Exchange.asmx");

	private static ExchangeService service;

	/**
	 * Default constructor. Use this to specify the password in the text file or
	 * property file.
	 */
	public ExchangeEntry() {
		this.domain = ExchangeEntry.defaultDomain;
		setUp();
	}

	/**
	 * Initiates the service and the connection.
	 * 
	 * @return true is connection is established.
	 */
	private boolean setUp() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(propertyFile);
			properties.load(fis);

			gasparUserName = properties.getProperty("username");
			gasparPassword = properties.getProperty("password");
			fis.close();

			if (gasparUserName != null) {

				service = new ExchangeService();

				ExchangeCredentials credentials = new WebCredentials(
						gasparUserName, gasparPassword, domain);
				service.setCredentials(credentials);
				try {
					service.autodiscoverUrl(emailAddress);
				} catch (Exception e) {
					e.printStackTrace();
					service.setUrl(EWAURI);
				}
			} else {
				System.err
						.println("Cannot find property file with gaspar credentials for EWA set up");
			}
		} catch (FileNotFoundException e1) {
			System.err.println("Cannot load property file");
			return false;
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Cannot load property file");
			return false;
		}
		return (service.getServerInfo() != null);
	}

	/**
	 * Retrieves all the EmailAddress (Name + email) that satifies a given
	 * string constraint.
	 * 
	 * @param nameToResolve
	 *            a string constraint (don't accept regex).
	 * @return a list of EmailAddress containing all the results.
	 */
	public List<EmailAddress> getUsers(String nameToResolve) {
		ResolveNameSearchLocation searchScope = ResolveNameSearchLocation.ContactsThenDirectory;
		NameResolutionCollection nrc;
		List<EmailAddress> list = new ArrayList<EmailAddress>();
		try {
			nrc = service.resolveName(nameToResolve, searchScope, true);
			Iterator<NameResolution> iter = nrc.iterator();
			list = new ArrayList<EmailAddress>(nrc.getCount());
			while (iter.hasNext()) {
				NameResolution nr = iter.next();
				EmailAddress mail = nr.getMailbox();
				list.add(mail);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of occupancy for a given email and a given time
	 * window.
	 * 
	 * @param email
	 *            the smtp adress of the contact (must be unique and valide).
	 * @param mFrPeriod
	 *            the time window to check.
	 * @return a list of non-available time window.
	 */
	public List<FRPeriod> getAvailabilityFromEWAUID(String email,
			FRPeriod mFrPeriod) {
		List<AttendeeInfo> attendees = new ArrayList<AttendeeInfo>(1);
		attendees.add(new AttendeeInfo(email));
		Date start = new Date(mFrPeriod.getTimeStampStart());
		Date end = new Date(mFrPeriod.getTimeStampEnd());

		try {
			GetUserAvailabilityResults results = service.getUserAvailability(
					attendees, new TimeWindow(start, end),
					AvailabilityData.FreeBusy);

			int attendeeIndex = 0;
			List<FRPeriod> list = new ArrayList<FRPeriod>();
			for (AttendeeAvailability attendeeAvailability : results
					.getAttendeesAvailability()) {
				AttendeeInfo attendee = attendees.get(attendeeIndex);
				attendee.getSmtpAddress();
				System.out.println("Getting availability for "
						+ attendee.getSmtpAddress());
				if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
					for (CalendarEvent calendarEvent : attendeeAvailability
							.getCalendarEvents()) {

						Date ts = calendarEvent.getStartTime();
						long timestamp = ts.getTime();
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(timestamp);
						long offset = (mCalendar.get(Calendar.ZONE_OFFSET) + mCalendar
								.get(Calendar.DST_OFFSET)) / (60 * 1000);
						FRPeriod mFrPeriod2 = new FRPeriod(calendarEvent
								.getStartTime().getTime()
								+ offset
								* FRTimes.ONE_MIN_IN_MS, calendarEvent
								.getEndTime().getTime()
								+ offset
								* FRTimes.ONE_MIN_IN_MS);
						list.add(mFrPeriod2);
					}
				}
				attendeeIndex++;
				return list;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
