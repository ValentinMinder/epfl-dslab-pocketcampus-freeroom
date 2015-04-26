package org.pocketcampus.plugin.cloudprint.server;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class PrintJobChecker {
	private String printerName;
	private String lastState = "";
	private long lastChanged = 0;
	public PrintJobChecker(String printerName) {
		this.printerName = printerName;
	}
	public synchronized String checkJobStuck(final long STUCK_JOB_CHECK_PERIOD) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"lpstat", "-p", printerName});
		String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
		long now = System.currentTimeMillis();
		if(!lastState.equals(status)) {
			lastState = status;
			lastChanged = now;
		}
		if(now - lastChanged > 1000l * 60l * STUCK_JOB_CHECK_PERIOD && status.contains("now printing")) { // shit
			System.out.println(status);
			return StringUtils.substringBetween(status, "now printing ", ".");
		}
		return null;
	}
	private static boolean cancelJob(String jobId) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"cancel", jobId});
		String status = IOUtils.toString(proc.getErrorStream(), "UTF-8");
		boolean succeeded = status.trim().length() == 0;
		if(!succeeded) { // shit
			System.out.println(status);
		}
		return succeeded;
	}
	public void verifyJobAndTakeAction(final long STUCK_JOB_CHECK_PERIOD) throws IOException {
		String jobId = checkJobStuck(STUCK_JOB_CHECK_PERIOD);
		if(jobId != null) {
			boolean result = cancelJob(jobId);
			System.out.println("JOB WAS STUCK. TRIED TO CANCEL IT. result=" + result);
		}
	}
}
