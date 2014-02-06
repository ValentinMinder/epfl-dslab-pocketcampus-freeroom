package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.plugin.isacademia.shared.*;

import org.apache.thrift.TException;

import org.joda.time.*;

import ch.epfl.tequila.client.model.TequilaPrincipal;

/**
 * Implementation of IsAcademiaService.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class IsAcademiaServiceImpl implements IsAcademiaService.Iface {
	private final HttpsClient _client;
	private final SessionManager _manager;
	private final Schedule _schedule;

	public IsAcademiaServiceImpl() {
		System.out.println("Starting IsAcademia server...");
		_client = new HttpsClientImpl();
		_manager = new SessionManager();
		_schedule = new ScheduleImpl(_client);
		
		new Thread(_manager.getCleaner()).start();
	}

	@Override
	public IsaTokenResponse getIsaTequilaToken() throws TException {
		String token = PocketCampusServer.authGetTequilaToken("isacademia");
		if(token == null)
			return new IsaTokenResponse(IsaStatusCode.NETWORK_ERROR);
		return new IsaTokenResponse(IsaStatusCode.OK).setTequilaToken(token);
	}

	@Override
	public IsaSessionResponse getIsaSessionId(String tequilaToken) throws TException {
		try {
			TequilaPrincipal principal = PocketCampusServer.authGetTequilaPrincipal(tequilaToken);
			if(principal == null)
				return new IsaSessionResponse(IsaStatusCode.NETWORK_ERROR);
			String session = _manager.insert(principal.getUser(), principal.getAttribute("uniqueid"));
			return new IsaSessionResponse(IsaStatusCode.OK).setSessionId(session);
			
		} catch(SecurityException e) {
			return new IsaSessionResponse(IsaStatusCode.INVALID_SESSION);
		}
	}
	
	@Override
	public ScheduleResponse getSchedule(ScheduleRequest req) throws TException {
		LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
		String lang = req.isSetLanguage() ? req.getLanguage() : "fr";

		try {
			return _schedule.get(date, lang, _manager.getSciper(req.getSessionId()));
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	private static LocalDate getCurrentWeekStart() {
		return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
	}

}