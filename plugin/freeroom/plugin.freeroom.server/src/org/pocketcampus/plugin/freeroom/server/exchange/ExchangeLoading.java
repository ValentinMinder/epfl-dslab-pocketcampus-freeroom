package org.pocketcampus.plugin.freeroom.server.exchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.pocketcampus.platform.shared.utils.StringUtils;

/**
 * This class is used to update the rooms list with correct attributes in the
 * database (i.e EWAid)
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class ExchangeLoading {

	private String DB_USERNAME;
	private String DB_PASSWORD;
	private String DB_URL;

	private static String sep = File.separator;
	private static String path = "src" + sep + "org" + sep + "pocketcampus"
			+ sep + "plugin" + sep + "freeroom" + sep + "server" + sep
			+ "exchange" + sep + "EWSRoomsData";

	public ExchangeLoading(String DB_URL, String DB_USER, String DB_PWD) {
		this.DB_URL = DB_URL;
		this.DB_USERNAME = DB_USER;
		this.DB_PASSWORD = DB_PWD;
	}

	/**
	 * Load all the data in all files in the path.
	 * 
	 * @return
	 */
	public boolean loadExchangeData() {

		File exchangeDirectory = new File(path);
		if (!exchangeDirectory.exists()) {
			System.err.println("Error: directory of data don't exists.");
			return false;
		}
		File[] array = exchangeDirectory.listFiles();
		boolean flag = true;
		for (File file : array) {
			if (!file.getName().startsWith(".")) {
				boolean result = loadExchangeData(file);
				flag = flag && result;
				if (!result) {
					System.err.println("Problem in file: " + file.getName());
				}
			}
		}

		return flag;
	}

	/**
	 * Load the data from a file. Rooms (contacts) are comma-separated.
	 * 
	 * @param file
	 * @return true if successful, false in case of errors.
	 */
	private boolean loadExchangeData(File file) {
		if (!file.exists() || !file.canRead() || file.getName().startsWith(".")) {
			return false;
		}
		boolean flag = true;
		try {
			String s = StringUtils.fromStream(new FileInputStream(file), "UTF-8");
			String[] tab = s.split("[,;]");
			for (String string : tab) {
				boolean result = loadExchangeData(string);
				flag = flag && result;
				if (!result) {
					System.err.println("Problem with string: " + string);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return flag;
	}

	/**
	 * Load the given room as a contact (name + email).
	 * 
	 * @param nameEmail
	 *            contact of the room (usually: "Name <name@intranet.epfl.ch>").
	 * @return true if successful, false in case of errors.
	 */
	private boolean loadExchangeData(String nameEmail) {
		nameEmail = nameEmail.trim();
		String[] details = nameEmail.split("[<>]");
		if (details.length < 2) {
			System.err.println("Error: malformed String " + nameEmail);
			return false;
		}
		String name = details[0].trim();
		String email = details[1];
		return loadExchangeData(name, email);
	}

	/**
	 * Load the given EWAid for the room, given by it's door code. All the
	 * spaces are removed to match DB "doorCodeWithoutSpace" field.
	 * 
	 * @param name
	 *            the door code of the room (usually "BC 01");
	 * @param email
	 *            the EWAid (usually "bc01@intranet.epfl.ch")
	 * @return true if successful, false in case of errors.
	 */
	private boolean loadExchangeData(String name, String email) {
		String concatName = name.replaceAll("\\s", "").toUpperCase();
		if (concatName.length() == 0) {
			System.err
					.println("invalid name: " + name + " with email:" + email);
			return false;
		}

		ExchangeServiceImpl exchange = new ExchangeServiceImpl(DB_URL,
				DB_USERNAME, DB_PASSWORD, null);
		return exchange.setExchangeData(concatName, email);
	}
}
