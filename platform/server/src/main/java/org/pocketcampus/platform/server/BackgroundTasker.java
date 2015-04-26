package org.pocketcampus.platform.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.platform.server.launcher.ServiceInfo;
import org.pocketcampus.platform.shared.utils.StringUtils;

public class BackgroundTasker {
	
	private class Task {
		public Runnable task;
		public long period;
		public long lastRun;
		public String pluginName;
		public Task(Runnable task, long period, long lastRun, String pluginName) {
			this.task = task;
			this.period = period;
			this.lastRun = lastRun;
			this.pluginName = pluginName;
		}
	}
	
	public class Scheduler {
		private String pluginName;
		Scheduler(String pluginName) {
			this.pluginName = pluginName;
		}
		public void addTask(long periodInMillis, boolean runAtStartup, Runnable runnable) {
			BackgroundTasker.this.addTask(pluginName, periodInMillis, runAtStartup, runnable);
		}
	}
	
	private List<Task> tasks = new LinkedList<>();	

	public BackgroundTasker(List<ServiceInfo> plugins) {
		for(ServiceInfo s : plugins) {
			if(s.taskRunner != null) {
				s.taskRunner.schedule(new Scheduler(s.name));
			}
		}
	}
	
	private void addTask(String pluginName, long periodInMillis, boolean runAtStartup, Runnable runnable) {
		tasks.add(new Task(runnable, periodInMillis, runAtStartup ? 0l : System.currentTimeMillis(), pluginName));
	}
	
	public Runnable getRunnable() {
		return new Runnable() {
			private String serverName = "unknown";
			public void run() {
				try {
					Process proc = Runtime.getRuntime().exec(new String[]{ "hostname" });
					serverName = StringUtils.fromStream(proc.getInputStream(), "UTF-8");
				} catch (Throwable e) {
					e.printStackTrace();
				}
				try {
					ronronne();
				} catch (Throwable e) {
					e.printStackTrace();
					sendEmail(
							"ACTION REQUIRED background runner died on " + serverName, 
							"<pre>" + exceptionToString(e) + "</pre>");
				}
			}
			private void ronronne() {
				
				while(true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(Task t : tasks) {
						if(System.currentTimeMillis() - t.lastRun > t.period) {
							t.lastRun = System.currentTimeMillis();
							try {
								t.task.run();
							} catch (Throwable e) {
								e.printStackTrace();
								sendEmail(
										"crash in background runner in " + t.pluginName + " on " + serverName, 
										"<pre>" + exceptionToString(e) + "</pre>");
							}
						}
					}

				}
			}
		};
	}
	
	private static void sendEmail(String title, String body) {
		boolean res = EmailSender.openSession();
		res = res && EmailSender.sendEmail("alarm@pocketcampus.org", title, body);
		res = res && EmailSender.closeSession();
		if(!res) {
			System.err.println("DAMN! we couldn't even send an alarm email. we're screwed");
		}
	}
	
	private static String exceptionToString(Throwable t) {
		StringWriter e = new StringWriter();
		t.printStackTrace(new PrintWriter(e));
		return e.toString();
	}
	
}
