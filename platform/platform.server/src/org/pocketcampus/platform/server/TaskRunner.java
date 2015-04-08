package org.pocketcampus.platform.server;


public interface TaskRunner {
	void schedule(BackgroundTasker.Scheduler tasker);
}
