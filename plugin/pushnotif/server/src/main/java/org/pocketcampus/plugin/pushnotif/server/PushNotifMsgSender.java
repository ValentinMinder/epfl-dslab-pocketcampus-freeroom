package org.pocketcampus.plugin.pushnotif.server;

import com.google.android.gcm.server.*;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;
import org.json.JSONException;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushNotifMsgSender {

	private static final int MULTICAST_SIZE = 1000;

	private static final long APNS_FEEDBACK_SERVICE_REQ_MIN_PERIOD = 86400; // seconds
																			// ==
																			// 24
																			// hours

	private static final String APNS_P12_PATH = PocketCampusServer.CONFIG
			.getString("APNS_P12_PATH");

	private static final String APNS_P12_PASSWORD = PocketCampusServer.CONFIG
			.getString("APNS_P12_PASSWORD");

	private static final Boolean APNS_PROD = new Boolean(
			PocketCampusServer.CONFIG.getString("APNS_PROD"));

	private static long apnsFeedbackServiceLastCheckTimestamp = 0;

	private static final Sender sender = new Sender(
			PocketCampusServer.CONFIG.getString("GCM_SERVER_KEY"));

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	private static final Logger logger = Logger
			.getLogger(PushNotifServiceImpl.class.getName());

	public static void sendToAndroidDevices(final PushNotifDataStore dataStore,
			final Map<String, String> androidDevices, Map<String, String> msg) {
		// must not send to more than 1000 devices at a time (GCM limit)
		
		Message.Builder messageBldr = new Message.Builder();
		for(String k : msg.keySet())
			messageBldr.addData(k, msg.get(k));
		final Message message = messageBldr.build();
				
		if(androidDevices.size() > MULTICAST_SIZE)
			logger.log(Level.SEVERE, "Cannot send more than " + MULTICAST_SIZE + "messages at a time, please chunk request");

		logger.info("Asynchronously sending " + androidDevices.size() + " ANDROID push notification messages");
				
		if(androidDevices.size() == 0)
			return;
		
		threadPool.execute(new Runnable() {

			public void run() {
				// Message message = new Message.Builder().build();
				
				/* Android */
				
				MulticastResult multicastResult;
				List<String> deviceList = new ArrayList<String>(androidDevices.keySet());
				Set<String> users = new HashSet<String>(androidDevices.values());
				try {
					multicastResult = sender.send(message, deviceList, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < deviceList.size(); i++) {
					String regId = deviceList.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						users.remove(androidDevices.get(regId));
						logger.fine("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							// Datastore.updateRegistration(regId,
							// canonicalRegId);
							dataStore.updatePushToken(
									"ANDROID", regId,
									canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							// Datastore.unregister(regId);
							dataStore.deletePushToken(
									"ANDROID", regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
				
				
				if (users.size() > 0)
					PushNotifServiceImpl.pushNotifNotifyFailedUsers(message.getData().get("pluginName"), new ArrayList<String>(users));
				
			}
		});
	}
	
	public static void sendToiOSDevices(final PushNotifDataStore dataStore,
			final Map<String, String> devices, final Map<String, String> msg) {
	
		logger.info("Asynchronously sending " + devices.size() + " IOS push notification messages");
		
		if(devices.size() == 0)
			return;
			
		threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				logger.info("Sending notifications to iOS devices...");
				PushNotificationPayload payload = PushNotificationPayload.complex();
				List<String> deviceList = new ArrayList<String>(devices.keySet());
				Set<String> users = new HashSet<String>(devices.values());
				try {
					for(String k : msg.keySet()) {
						if(k.equals("alert"))
							payload.addAlert(msg.get(k));
						else if(k.equals("sound"))
							payload.addSound(msg.get(k)); // "default"
						else if(k.equals("badge"))
							payload.addBadge(Integer.parseInt(msg.get(k)));
						else
							payload.addCustomDictionary(k, msg.get(k));
					}
					List<PushedNotification> notifications = Push.payload(payload,
							APNS_P12_PATH, APNS_P12_PASSWORD, APNS_PROD, deviceList);
					for (PushedNotification notif : notifications) {
						if (notif.isSuccessful()) {
							users.remove(devices.get(notif.getDevice().getToken()));
						} else {
							String errorMessage = "Failed:" + notif.getDevice().getToken();
							
							ResponsePacket errorResponse = notif.getResponse();
		                    if (errorResponse != null) {
		                            errorMessage += "\nError:"+errorResponse.getMessage();
		                    }
		                    dataStore.deletePushToken("IOS", notif.getDevice().getToken());
		                    errorMessage += "\n deviceToken removed from DB ("+notif.getDevice().getToken()+")";
							logger.info(errorMessage);
						}
					}

				} catch (JSONException e) {
					logger.info("JSONException while creating notification payload");
					e.printStackTrace();
				} catch (CommunicationException e) {
					logger.info("CommunicationException while sending notification");
					e.printStackTrace();
				} catch (KeystoreException e) {
					logger.info("Keystore while creating notification");
					e.printStackTrace();
				}
				
				/* Contact APNS Feedback Service if necessary */
				if ((System.currentTimeMillis() / 1000) - apnsFeedbackServiceLastCheckTimestamp > APNS_FEEDBACK_SERVICE_REQ_MIN_PERIOD) {
					// We do not announce the failed token list that we get here because
					// (1) they might be old and we don't know to which user they correspond
					// (2) the corresponding user might be a user of another plugin (not the plugin that issued this call)
					// Nevertheless we keep the function call in order to cleanup the DB 
					// TODO check if there's a better way to do it
					contactAPNSFeedbackServiceAndPurgeDB(dataStore);
					//List<String> failedFeedback = contactAPNSFeedbackServiceAndPurgeDB(dataStore);
					//failed.addAll(failedFeedback);
				}
				

				if (users.size() > 0)
					PushNotifServiceImpl.pushNotifNotifyFailedUsers(msg.get("pluginName"), new ArrayList<String>(users));
				
				
			}
		});
		
			
		
	}
	
	private static List<String> contactAPNSFeedbackServiceAndPurgeDB(PushNotifDataStore dataStore) {
		logger.info("Contacting APNS Feedback Service to remove inactive devices...");
		try {
			List<Device> inactiveDevices = Push.feedback(APNS_P12_PATH, APNS_P12_PASSWORD, APNS_PROD);
			LinkedList<String> retInactiveDevices = new LinkedList<String>();
			for (Device device : inactiveDevices) {
				dataStore.deletePushToken("IOS", device.getToken());
				retInactiveDevices.add(device.getToken());
			}
			logger.info(inactiveDevices.size()+" device(s) removed from DB");
			apnsFeedbackServiceLastCheckTimestamp = System.currentTimeMillis() / 1000;
			return retInactiveDevices;
		} catch (CommunicationException e) {
			logger.info("CommunicationException while contacting APNS Feedback Service");
			e.printStackTrace();
		} catch (KeystoreException e) {
			logger.info("KeystoreException while contacting APNS Feedback Service");
			e.printStackTrace();
		}
		return new LinkedList<String>(); //empty list if error
	}

}
