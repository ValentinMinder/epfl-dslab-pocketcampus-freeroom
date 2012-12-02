package org.pocketcampus.plugin.pushnotif.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifResponse;
import org.pocketcampus.plugin.pushnotif.shared.PlatformType;

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

	public static void sendToDevices(PushNotifDataStore dataStore,
			List<String> androidDevices, List<String> iosDevices, String plugin, String msg) {
		
		/* Android */
		
		Message message = new Message.Builder().addData("pluginName", plugin)
				.addData("pluginMessage", msg).build();
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
		
		/* iOS */
		/* No need to chunk iOS tokens (JAVAPNS takes care of it) */
		
		asyncSendiOS(dataStore, iosDevices, plugin, msg);
		logger.info("Asynchronously sending notification to "+iosDevices.size()+" iOS devices");
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
									PlatformType.PC_PLATFORM_ANDROID, regId,
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
									PlatformType.PC_PLATFORM_ANDROID, regId);
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
							"appendToFailedDevicesList", new PushNotifResponse(
									failed));
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
	
	private static void asyncSendiOS(PushNotifDataStore dataStore_,
			List<String> devices_, String plugin_, String msg_) {
		
		if (devices_.size() > 0) {
			
			final PushNotifDataStore dataStore = dataStore_;
			final List<String> devices = devices_;
			final String plugin = plugin_;
			final String msg = msg_;
			
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					logger.info("Sending notifications to iOS devices (plugin: "+plugin+", msg: "+msg+")...");
					LinkedList<String> failed = new LinkedList<String>();
					PushNotificationPayload payload = PushNotificationPayload.complex();
					try {
						payload.addAlert(msg);
						payload.addSound("default");
						payload.addCustomDictionary("pluginName", plugin);
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
			                    dataStore.deletePushToken(PlatformType.PC_PLATFORM_IOS, notif.getDevice().getToken());
			                    errorMessage += "\n deviceToken removed from DB ("+notif.getDevice().getToken()+")";
								logger.info(errorMessage);
								failed.add(notif.getDevice().getToken());
							}
						}

					} catch (JSONException e) {
						logger.info("JSONException while creating notification payload "
								+ msg + " for plugin " + plugin);
						e.printStackTrace();
					} catch (CommunicationException e) {
						logger.info("CommunicationException while sending notification "
								+ msg + " for plugin " + plugin);
						e.printStackTrace();
					} catch (KeystoreException e) {
						logger.info("Keystore while creating notification " + msg
								+ " for plugin " + plugin);
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
						PocketCampusServer.invokeOnPlugin(plugin,"appendToFailedDevicesList", new PushNotifResponse(failed));
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
				dataStore.deletePushToken(PlatformType.PC_PLATFORM_IOS, device.getToken());
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
