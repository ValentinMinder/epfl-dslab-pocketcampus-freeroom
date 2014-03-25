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
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

public class ExchangeEntry {
	private String gasparUserName = null;
	private String gasparPassword = null;
	private String emailAddress = "valentin.minder@epfl.ch";
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

	public ExchangeEntry(String gaspar, String password) {
		this.gasparUserName = gaspar;
		this.gasparPassword = password;
		this.domain = ExchangeEntry.defaultDomain;
		setUp();
	}

	public ExchangeEntry(String gaspar, String password, String domain) {
		gasparUserName = gaspar;
		gasparPassword = password;
		this.domain = domain;
		setUp();
	}

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

	public List<FRPeriod> getAvailability(FRRoom mFRRoom, FRPeriod mFrPeriod) {
		// TODO: to delete, use the EWA property in the database, not this!
		String email = mFRRoom.getDoorCode();
		email.replaceAll("\\s", "");
		email += "@intranet.epfl.ch";
		return getAvailabilityFromEWAUID(email, mFrPeriod);
		// TODO: uncomment this
		// return getAvailabilityFromRoomUID(mFRRoom.getUid(), mFrPeriod);
	}

	public List<FRPeriod> getAvailabilityFromRoomUID(String uid,
			FRPeriod mFrPeriod) {
		// TODO: use the EWA property in the database, not this!
		String email = ""; // TODO: method to query the database, to get the ewa
							// id from the ui
		return getAvailabilityFromEWAUID(email, mFrPeriod);
	}

	public List<FRPeriod> getAvailabilityFromEWAUID(String email,
			FRPeriod mFrPeriod) {
		List<AttendeeInfo> attendees = new ArrayList<AttendeeInfo>();
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
				System.out.println("Availability for "
						+ attendees.get(attendeeIndex).getSmtpAddress());
				if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
					for (CalendarEvent calendarEvent : attendeeAvailability
							.getCalendarEvents()) {
						FRPeriod mFrPeriod2 = new FRPeriod(calendarEvent
								.getStartTime().getTime(), calendarEvent
								.getEndTime().getTime(), false);
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
