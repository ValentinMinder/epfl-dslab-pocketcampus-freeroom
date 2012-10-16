package org.pocketcampus.plugin.pushnotif.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.pushnotif.shared.TequilaToken;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifReply;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifRequest;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

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

	private static final int MULTICAST_SIZE = 1000;

	private static final Sender sender = new Sender("AIzaSyBw2ORa_rjo4J8y1_2_MTtYSm6wqrxU8I0");

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	private static final Logger logger = Logger.getLogger(PushNotifServiceImpl.class.getName());

	public PushNotifServiceImpl() {
		System.out.println("Starting PushNotif plugin server ...");
	}

	public Boolean securityCheck(String ip, String method) {
		System.out.println("securityCheck");
		if(method.startsWith("secure") && !ip.startsWith("127"))
			return false;
		return true;
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
	public PushNotifReply registerPushNotif(PushNotifRequest aPushNotifRequest)
			throws TException {
		System.out.println("registerPushNotif");
		if (!aPushNotifRequest.isSetIAuthenticatedToken()) {
			return new PushNotifReply(404);
		}
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
		switch (aPushNotifRequest.getIPlatformType()) {
		case PC_PLATFORM_ANDROID:
			System.out.println("principal = " + principal);
			if (!aPushNotifRequest.isSetIAndroidRegistrationId()) {
				return new PushNotifReply(404);
			}
			System.out.println("regId = "
					+ aPushNotifRequest.getIAndroidRegistrationId());
			return new PushNotifReply(200);
		case PC_PLATFORM_IOS:
			// TODO
			return new PushNotifReply(200);
		default:
			return new PushNotifReply(404);
		}
	}

	@Override
	public PushNotifReply unregisterPushNotif(PushNotifRequest aPushNotifRequest)
			throws TException {
		System.out.println("unregisterPushNotif");
		// TODO
		return null;
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
		// config.setAllows("categorie=epfl-guests");
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

	static {
		//doSend();
	}
	
	/**
	 * Helper func to send message to device
	 */
	
	private static synchronized void doSend() {
		List<String> devices = new ArrayList<String>();
		devices.add("APA91bGc1ZAQQ-DScg4cn9JLK72-uve3Xm5RFMhgqgGZmP9nUycyPrV3QmwvRo0-L4GBZqKwFK-gUXUjVt7Y7ln7_hhZxzoX3lv-p_WF4oJSXJSJSAC3512OwNOL8D5cnckDaFjwt4VspssxEbwv0leOshixUdX3DXdp-Z1zHTsne0pIxjEM35I");
		sendMsg(devices, "test", "testos");
	}
	
	private static void sendMsg(List<String> devices, String plugin, String msg) {

		Message message = new Message.Builder().addData(plugin, msg).build();
		// send a multicast message using JSON
		// must split in chunks of 1000 devices (GCM limit)
		int total = devices.size();
		List<String> partialDevices = new ArrayList<String>(total);
		int counter = 0;
		int tasks = 0;
		for (String device : devices) {
			counter++;
			partialDevices.add(device);
			int partialSize = partialDevices.size();
			if (partialSize == MULTICAST_SIZE || counter == total) {
				asyncSend(partialDevices, message);
				partialDevices.clear();
				tasks++;
			}
		}
		logger.info("Asynchronously sending " + tasks
				+ " multicast messages to " + total + " devices");
	}

	private static void asyncSend(List<String> partialDevices, Message dupMessage) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);
		final Message message = dupMessage;
		threadPool.execute(new Runnable() {

			public void run() {
				// Message message = new Message.Builder().build();
				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						logger.fine("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							//Datastore.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							//Datastore.unregister(regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
			}
		});
	}

}
