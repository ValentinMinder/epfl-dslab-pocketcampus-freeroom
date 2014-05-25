package org.pocketcampus.plugin.freeroom.server.exchange;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ExchangeLoading {

	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DBMS_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	final static String DB_URL = "jdbc:mysql://localhost/pocketcampus?allowMultiQueries=true";

	private static String s = File.separator;
	private static String path = "src" + s + "org" + s + "pocketcampus" + s
			+ "plugin" + s + "freeroom" + s + "server" + s + "exchange" + s
			+ "EWSRoomsData";

	/**
	 * This method should be run periodically to update the EWAid in the room
	 * list.
	 * 
	 * It should be run to add new EWAid from the text file in EWSRoomData (see
	 * path), or if the database has been deleted.
	 * 
	 * You can also add manually the entry in the DB, but this is to avoid,
	 * because if something bad append to the DB, this loader wont be able to
	 * recrete deleted EWAid.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean status = loadExchangeData();
		System.out.println("Updating exchange ID in rooms DB");
		if (status) {
			System.out.println("Database successfully update with EWA data!");
		} else {
			System.err.println("At least one entry was not sucessful!");
		}

	}

	/**
	 * Load all the data in all files in the path.
	 * 
	 * @return
	 */
	public static boolean loadExchangeData() {

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
	 * @param file
	 * @return true if successful, false in case of errors.
	 */
	private static boolean loadExchangeData(File file) {
		if (!file.exists() || !file.canRead() || file.getName().startsWith(".")) {
			return false;
		}
		boolean flag = true;
		try {
			String s = IOUtils.toString(new FileReader(file));
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
	 * @param nameEmail
	 *            contact of the room (usually: "Name <name@intranet.epfl.ch>").
	 * @return true if successful, false in case of errors.
	 */
	private static boolean loadExchangeData(String nameEmail) {
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
	private static boolean loadExchangeData(String name, String email) {
		String concatName = name.replaceAll("\\s", "").toUpperCase();
		if (concatName.length() == 0) {
			System.err
					.println("invalid name: " + name + " with email:" + email);
			return false;
		}

		ExchangeServiceImpl exchange = new ExchangeServiceImpl(DB_URL, DB_USERNAME, DB_PASSWORD, null);
		return exchange.setExchangeData(concatName, email);
	}
}
