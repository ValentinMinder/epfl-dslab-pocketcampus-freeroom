package org.pocketcampus.plugin.pushnotif.server;

import java.io.IOException;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.authentication.TequilaToken;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifRequest;
import org.pocketcampus.plugin.pushnotif.shared.PlatformType;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifRegReq;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifReply;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

/**
 * PushNotifServiceImpl
 * 
 * The implementation of the server side of the PushNotif Plugin.
 * 
 * It routes the push notifs of other plugins to the user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class PushNotifServiceImpl implements PushNotifService.Iface {

	private PushNotifDataStore dataStore;

	public PushNotifServiceImpl() {
		System.out.println("Starting PushNotif plugin server ...");
		dataStore = new PushNotifDataStore();
	}

	public Boolean securityCheck(String ip, String method) {
		System.out.println("securityCheck");
		if(method.startsWith("secure") && !ip.startsWith("127"))
			return false;
		return true;
	}
	
	public void pushMessage(PushNotifRequest req) {
		System.out.println("pushMessage");
		List<String> androidTokens = dataStore.selectTokens(req.getGasparList(), PlatformType.PC_PLATFORM_ANDROID);
		List<String> iosTokens = dataStore.selectTokens(req.getGasparList(), PlatformType.PC_PLATFORM_IOS);
		if(androidTokens == null || iosTokens == null)
			return;
		PushNotifMsgSender.sendToDevices(dataStore, androidTokens, iosTokens, req.getPluginName(), req.getMessage());
	}
	
	@Override
	public TequilaToken getTequilaTokenForPushNotif() throws TException {
		System.out.println("getTequilaTokenForPushNotif");
		
		try {
			return new TequilaToken(getTokenFromTequila());
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from Tequila");
		}
	}

	@Override
	public PushNotifReply registerPushNotif(PushNotifRegReq aPushNotifRequest)
			throws TException {
		System.out.println("registerPushNotif");
		/*if (!aPushNotifRequest.isSetIAuthenticatedToken()) {
			return new PushNotifReply(404);
		}*/
		TequilaPrincipal principal = null;
		try {
			principal = getTequilaPrincipal(aPushNotifRequest
					.getIAuthenticatedToken().getITequilaKey());
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaPrincipal from Tequila");
		}
		if (principal == null) {
			return new PushNotifReply(405);
		}
		// principal = [user=chamsedd, org=EPFL, host=128.178.236.75,
		// attributes={phone=+41 21 6938188, status=ok, firstname=Amer,
		// where=IN-MA1/IN-S/ETU/EPFL/CH, requesthost=128.178.77.233,
		// version=2.1.1, unit=IN-MA1,Section d'informatique - Master semestre
		// 1, uniqueid=211338, username=chamsedd,chamsedd@in-ma1,
		// email=amer.chamseddine@epfl.ch, name=Chamseddine, authorig=cookie,
		// unixid=112338, groupid=30132}]
		/**
		 * principal = [user=self@accandme.com, org=EPFL, host=128.179.148.89, 
		 * attributes={status=ok, firstname=Amer, 
		 * where=EPFL-GUESTS/CH, requesthost=128.178.77.233, version=2.1.1, 
		 * unit=epfl-guests,EPFL Guests, 
		 * uniqueid=G17095, 
		 * username=self@accandme.com,G17095, 
		 * email=self@accandme.com, 
		 * name=Chamseddine, authorig=cookie, 
		 * unixid=517095, groupid=500000}]
		 * regId = APA91bGs1MJdynn2OIUsyw3EJrTRnFl4XHvPwwEDt8iSj--1l09jzVMB90jnW9dsKrYF1hHhtaRvwfnVtN6VGIL7oaBzDoaBNj4w1IU9ZbfWaaPmY1zls0ZBeZ9sUl5PmHCUtgtetQorCAr8P_7BVBRuuBzWmBMHcm9Je-NeGVbZFxPrliO6qQQ
		 */
		
		boolean st;
		st = dataStore.insertUser(principal);
		if(!st)
			return new PushNotifReply(504);
		st = dataStore.insertPushToken(principal.getUser(), aPushNotifRequest.getIPlatformType(), aPushNotifRequest.getRegistrationId());
		if(!st)
			return new PushNotifReply(504);
		return new PushNotifReply(200);
		
		
		/*switch (aPushNotifRequest.getIPlatformType()) {
		case PC_PLATFORM_ANDROID:
			System.out.println("principal = " + principal);
			if (!aPushNotifRequest.isSetIAndroidRegistrationId()) {
				return new PushNotifReply(404);
			}
			System.out.println("regId = "
					+ aPushNotifRequest.getIAndroidRegistrationId());
			return new PushNotifReply(200);
		case PC_PLATFORM_IOS:
			return new PushNotifReply(200);
		default:
			return new PushNotifReply(404);
		}*/
	}

	/**
	 * Helper function to get a TequilaKey from Tequila for PocketCampus
	 */
	private String getTokenFromTequila() throws IOException {
		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");
		// config.setOrg("PocketCampus");
		config.setService("PocketCampus Push Notification Service");
		config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where");
		config.setAllows("categorie=epfl-guests");
		// config.setAllows("categorie=Shibboleth");
		return TequilaService.instance().createRequest(config,
				"pocketcampus://pushnotif.pocketcampus.org");
	}

	/**
	 * Helper function to get user data from tequila
	 */
	private TequilaPrincipal getTequilaPrincipal(String teqKey)
			throws IOException {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setHost("tequila.epfl.ch");
		try {
			return TequilaService.instance().validateKey(clientConfig, teqKey);
		} catch (SecurityException e) {
			return null;
		}
	}


}
