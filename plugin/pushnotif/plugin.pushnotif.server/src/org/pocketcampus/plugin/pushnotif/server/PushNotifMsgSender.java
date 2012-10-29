package org.pocketcampus.plugin.pushnotif.server;

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

import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifResponse;
import org.pocketcampus.plugin.pushnotif.shared.PlatformType;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

public class PushNotifMsgSender {

	private static final int MULTICAST_SIZE = 1000;

	private static final Sender sender = new Sender(PC_SRV_CONFIG.getString("GCM_SERVER_KEY"));

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	private static final Logger logger = Logger.getLogger(PushNotifServiceImpl.class.getName());
	
	public static void sendToAndroidDevices(PushNotifDataStore dataStore, List<String> devices, String plugin, String msg) {
		Message message = new Message.Builder().addData("pluginName", plugin).addData("pluginMessage", msg).build();
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
				asyncSend(dataStore, partialDevices, message);
				partialDevices.clear();
				tasks++;
			}
		}
		logger.info("Asynchronously sending " + tasks
				+ " multicast messages to " + total + " devices");
	}

	private static void asyncSend(PushNotifDataStore tempDataStore, List<String> partialDevices, Message dupMessage) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);
		final Message message = dupMessage;
		final PushNotifDataStore dataStore = tempDataStore;
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
				LinkedList<String> failed = new LinkedList<String>();
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
							dataStore.updatePushToken(PlatformType.PC_PLATFORM_ANDROID, regId, canonicalRegId);
						}
					} else {
						failed.add(regId);
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							//Datastore.unregister(regId);
							dataStore.deletePushToken(PlatformType.PC_PLATFORM_ANDROID, regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
				if(failed.size() == 0)
					return;
				try {
					PocketCampusServer.invokeOnPlugin(message.getData().get("pluginName"), "appendToFailedDevicesList", new PushNotifResponse(failed));
					// fail silently because if the caller doesn't care about who fails, then screw him
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
