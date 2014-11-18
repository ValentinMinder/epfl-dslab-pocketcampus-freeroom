package org.pocketcampus.platform.server;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.pocketcampus.platform.server.launcher.ServiceInfo;

public class BackgroundChecker {

	public static Runnable getChecker(final List<ServiceInfo> plugins) {
		return new Runnable() {
			public void run() {
				String serverName = "unknown";
				try {
					Process proc = Runtime.getRuntime().exec(new String[]{ "hostname" });
					serverName = IOUtils.toString(proc.getInputStream(), "UTF-8");
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				String lastState = "";
				while(true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					StringBuilder sb = new StringBuilder();
					for (ServiceInfo service : plugins) {
						if (service.stateChecker != null) {
							int status = -1;
							try {
								status = service.stateChecker.checkState();
							} catch (Throwable e) {
								e.printStackTrace();
							}
							if (status != 200) {
								sb.append("<li>plugin <b>" + service.name + "</b> returned status code <i>" + status + "</i></li>");
							}
						}
					}
					String currentState = sb.toString();
					if(!lastState.equals(currentState)) {
						lastState = currentState;
						sendEmail(currentState, serverName);
						sendSms(serverName);
					}

				}
			}
			private void sendEmail(String state, String serverName) {
				if("".equals(state))
					state = "all plugins seem to run normally";
				boolean res = EmailSender.openSession();
				res = res && EmailSender.sendEmail("alarm@pocketcampus.org", "server " + serverName + " state changed", state);
				res = res && EmailSender.closeSession();
				if(!res) {
					System.err.println("DAMN! we couldn't even send an alarm email. we're screwed");
				}
			}
			private void sendSms(String serverName) {
				boolean res = SmsSender.sendSms("server " + serverName + " state changed");
				if(!res) {
					System.err.println("DAMN! we couldn't even send an alarm SMS. we're screwed");
				}
			}
		};
	}
	
}
