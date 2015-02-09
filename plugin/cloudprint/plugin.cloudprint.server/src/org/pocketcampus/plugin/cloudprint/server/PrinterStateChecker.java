package org.pocketcampus.plugin.cloudprint.server;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class PrinterStateChecker {
	private static boolean checkPrinterEnabled(String printerName) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"lpstat", "-p", printerName});
		String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
		boolean enabled = status.contains("enabled");
		if(!enabled) { // shit
			System.out.println(status);
		}
		return enabled;
	}
	private static boolean enablePrinter(String printerName) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"cupsenable", printerName});
		String status = IOUtils.toString(proc.getErrorStream(), "UTF-8");
		boolean succeeded = status.trim().length() == 0;
		if(!succeeded) { // shit
			System.out.println(status);
		}
		return succeeded;
	}
	public static boolean checkPrinterAndTakeAction(String printerName) throws IOException {
		boolean enabled = checkPrinterEnabled(printerName);
		if(!enabled) {
			boolean result = enablePrinter(printerName);
			System.out.println("PRINTER WAS DISABLED. TRIED TO RE-ENABLE IT. result=" + result);
		}
		return enabled;
	}
}
