package org.pocketcampus.platform.server;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.pocketcampus.platform.server.launcher.ServiceInfo;

public class BackgroundChecker {

	public static Runnable getChecker(final List<ServiceInfo> plugins) {
		return new Runnable() {
			public void run() {
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
							} catch (IOException e) {
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
						sendEmail(currentState);
					}

				}
			}
			private void sendEmail(String state) {
				if("".equals(state))
					state = "all plugins seem to run normally";
				String server = "unknown";
				try {
					Process proc = Runtime.getRuntime().exec(new String[]{ "hostname" });
					server = IOUtils.toString(proc.getInputStream(), "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
				boolean res = EmailSender.openSession();
				res = res && EmailSender.sendEmail("alarm@pocketcampus.org", "server " + server + " state changed", state);
				res = res && EmailSender.closeSession();
				if(!res)
					System.err.println("DAMN! we couldn't even send an alarm email. we're screwed");

			}
		};
	}
	
}
