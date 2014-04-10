package org.pocketcampus.plugin.freeroom.server.exchange;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

public class ExchangeEntry {
	private String gasparUserName = "juweber";
	private String gasparPassword = "Djl1kdnvkjri4s]dk";
	private String emailAddress = "julien.weber@epfl.ch";
	private String domain = "intranet";

	private static String defaultDomain = "intranet";

	private static URI EWAURI;
	// should be new URI("https://ewa.epfl.ch/EWS/Exchange.asmx");

	private static ExchangeService service;

	public static void main(String[] args) {
		try {
			EWAURI = new URI("https://ewa.epfl.ch/EWS/Exchange.asmx");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// TODO: get the pass from the properties.
		ExchangeEntry ee = new ExchangeEntry("user", "mdp");

		// test something here...
	}

	/**
	 * Default constructor. Use this to specify the password in the text file or
	 * property file.
	 */
	public ExchangeEntry() {
		// TODO: get the pass from the server properties.
//		this.gasparUserName = "gaspar";
//		this.gasparPassword = "mdp";
		this.domain = ExchangeEntry.defaultDomain;
		setUp();
	}

	/**
	 * Constructor with specified gaspar and password ID. Default domain is
	 * used.
	 * 
	 * @param gaspar
	 *            epfl ID.
	 * @param password
	 *            gaspar password.
	 */
	public ExchangeEntry(String gaspar, String password) {
		this.gasparUserName = gaspar;
		this.gasparPassword = password;
		this.domain = ExchangeEntry.defaultDomain;
		setUp();
	}

	/**
	 * Constructor with specified gaspar ID, password and domain.
	 * 
	 * @param gaspar
	 *            epfl ID.
	 * @param password
	 *            gaspar password.
	 * @param domain
	 *            domain (intranet for students and staff).
	 */
	public ExchangeEntry(String gaspar, String password, String domain) {
		gasparUserName = gaspar;
		gasparPassword = password;
		this.domain = domain;
		setUp();
	}

	/**
	 * Initiates the service and the connection.
	 * 
	 * @return true is connection is established.
	 */
	private boolean setUp() {
		service = new ExchangeService();

		ExchangeCredentials credentials = new WebCredentials(gasparUserName,
				gasparPassword, domain);
		service.setCredentials(credentials);
		try {
			service.autodiscoverUrl(emailAddress);
		} catch (Exception e) {
			e.printStackTrace();
			service.setUrl(EWAURI);
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
	 * Retrieves the list of occupancy for a given email and a given time window.
	 * 
	 * @param email the smtp adress of the contact (must be unique and valide).
	 * @param mFrPeriod the time window to check.
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
				System.out.println(attendee.getSmtpAddress());
				if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
					for (CalendarEvent calendarEvent : attendeeAvailability
							.getCalendarEvents()) {
						FRPeriod mFrPeriod2 = new FRPeriod(calendarEvent
								.getStartTime().getTime(), calendarEvent
								.getEndTime().getTime(), false);
						System.out.println(calendarEvent
								.getStartTime() + "/" + calendarEvent
								.getEndTime());
						System.out.println(calendarEvent
								.getStartTime().getTime() + "/" + calendarEvent
								.getEndTime().getTime());
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
