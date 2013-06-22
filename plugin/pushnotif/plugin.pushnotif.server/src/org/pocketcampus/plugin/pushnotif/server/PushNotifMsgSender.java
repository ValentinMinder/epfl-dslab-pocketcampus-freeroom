package org.pocketcampus.plugin.pushnotif.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

import org.json.JSONException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class PushNotifMsgSender {

	private static final int MULTICAST_SIZE = 1000;

	private static final long APNS_FEEDBACK_SERVICE_REQ_MIN_PERIOD = 86400; // seconds
																			// ==
																			// 24
																			// hours

	private static final String APNS_P12_PATH = PC_SRV_CONFIG
			.getString("APNS_P12_PATH");

	private static final String APNS_P12_PASSWORD = PC_SRV_CONFIG
			.getString("APNS_P12_PASSWORD");

	private static final Boolean APNS_PROD = new Boolean(
			PC_SRV_CONFIG.getString("APNS_PROD"));

	private static long apnsFeedbackServiceLastCheckTimestamp = 0;

	private static final Sender sender = new Sender(
			PC_SRV_CONFIG.getString("GCM_SERVER_KEY"));

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	private static final Logger logger = Logger
			.getLogger(PushNotifServiceImpl.class.getName());

	public static void sendToAndroidDevices(PushNotifDataStore dataStore,
			Set<String> androidDevices, Map<String, String> msg) {
		
		Message.Builder messageBldr = new Message.Builder();
		for(String k : msg.keySet())
			messageBldr.addData(k, msg.get(k));
		Message message = messageBldr.build();
		
		// send a multicast message using JSON
		// must split in chunks of 1000 devices (GCM limit)
		int total = androidDevices.size();
		List<String> partialAndroidDevices = new ArrayList<String>(total);
		int counter = 0;
		int tasks = 0;
		for (String device : androidDevices) {
			counter++;
			partialAndroidDevices.add(device);
			int partialSize = partialAndroidDevices.size();
			if (partialSize == MULTICAST_SIZE || counter == total) {
				asyncSendAndroid(dataStore, partialAndroidDevices, message);
				partialAndroidDevices.clear();
				tasks++;
			}
		}
		logger.info("Asynchronously sending " + tasks
				+ " multicast messages to " + total + " Android devices");
		

	}


	private static void asyncSendAndroid(PushNotifDataStore tempDataStore,
			List<String> partialAndroidDevices, Message dupMessage) {
		// make a copy
		final List<String> androidDevices = new ArrayList<String>(partialAndroidDevices);
		final Message message = dupMessage;
		final PushNotifDataStore dataStore = tempDataStore;
		threadPool.execute(new Runnable() {

			public void run() {
				// Message message = new Message.Builder().build();
				
				/* Android */
				
				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, androidDevices, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				LinkedList<String> failed = new LinkedList<String>();
				// analyze the results
				for (int i = 0; i < androidDevices.size(); i++) {
					String regId = androidDevices.get(i);
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
							// Datastore.updateRegistration(regId,
							// canonicalRegId);
							dataStore.updatePushToken(
									"ANDROID", regId,
									canonicalRegId);
						}
					} else {
						failed.add(regId);
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
				
				
				if (failed.size() == 0)
					return;
				try {
					PocketCampusServer.invokeOnPlugin(
							message.getData().get("pluginName"),
							"appendToFailedDevicesList", failed);
					// fail silently because if the caller doesn't care about
					// who fails, then screw him
				} catch (NoSuchObjectException e) {
				} catch (SecurityException e) {
				} catch (IllegalArgumentException e) {
				} catch (NoSuchMethodException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
				
			}
		});
	}
	
	public static void sendToiOSDevices(PushNotifDataStore dataStore_,
			Set<String> devices_, Map<String, String> msg_) {
		
		logger.info("Asynchronously sending notification to "+devices_.size()+" iOS devices");
		
		if (devices_.size() > 0) {
			
			final PushNotifDataStore dataStore = dataStore_;
			final Set<String> devices = devices_;
			final Map<String, String> msg = msg_;
			
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					logger.info("Sending notifications to iOS devices...");
					LinkedList<String> failed = new LinkedList<String>();
					PushNotificationPayload payload = PushNotificationPayload.complex();
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
								APNS_P12_PATH, APNS_P12_PASSWORD, APNS_PROD, devices);
						for (PushedNotification notif : notifications) {
							if (notif.isSuccessful()) {
							} else {
								String errorMessage = "Failed:" + notif.getDevice().getToken();
								
								ResponsePacket errorResponse = notif.getResponse();
			                    if (errorResponse != null) {
			                            errorMessage += "\nError:"+errorResponse.getMessage();
			                    }
			                    dataStore.deletePushToken("IOS", notif.getDevice().getToken());
			                    errorMessage += "\n deviceToken removed from DB ("+notif.getDevice().getToken()+")";
								logger.info(errorMessage);
								failed.add(notif.getDevice().getToken());
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
					if ((System.currentTimeMillis() / 1000)
							- apnsFeedbackServiceLastCheckTimestamp > APNS_FEEDBACK_SERVICE_REQ_MIN_PERIOD) {
						List<String> failedFeedback = contactAPNSFeedbackServiceAndPurgeDB(dataStore);
						failed.addAll(failedFeedback);
					}
					
					if (failed.size() == 0)
						return;
					try {
						PocketCampusServer.invokeOnPlugin(msg.get("pluginName"),"appendToFailedDevicesList", failed);
						// fail silently because if the caller doesn't care about
						// who fails, then screw him
						// @Amer: Copy-pasting this comment made me happy :D
					} catch (NoSuchObjectException e) {
					} catch (SecurityException e) {
					} catch (IllegalArgumentException e) {
					} catch (NoSuchMethodException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
					
				}
			});
			
			
		}
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
