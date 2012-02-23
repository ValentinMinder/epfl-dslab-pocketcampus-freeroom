package org.pocketcampus.plugin.food.server.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses the contents of the Restaurant file which is stored on the server
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FeedUrlParser {

	/** The path to the file to parse */
	private String mFile;

	/** The feed Url */
	private String feed;

	/** The whole restaurant file in one String */
	private String feedString;

	/** Constructor for the RestaurantListParser */
	public FeedUrlParser(String file) {
		mFile = file;
		feedString = getContents();
		feed = restaurantFeed(feedString);
	}

	/** Returns the list of Strings and their corresponding Url */
	public String getFeed() {
		return feed;
	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 */
	private String getContents() {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			InputStream instream = this.getClass().getResourceAsStream(mFile);

			InputStreamReader inputreader = new InputStreamReader(instream);
			BufferedReader input = new BufferedReader(inputreader);

			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents.toString();
	}

	/**
	 * Constructs the list of restaurants and their corresponding feed url
	 * 
	 * @param restaurantList
	 *            the path to the file to parse
	 * @return a hashmap of Restaurant names and their String Url
	 */
	private String restaurantFeed(String restaurantList) {
		String tagMenu = "<Menu>";
		String tagUrl = "<rssfeed>";

		String tagMenuEnd = "</Menu>";
		String tagUrlEnd = "</rssfeed>";

		String feed = "";

		while (restaurantList.length() > 1) {
			// Menu
			int startResto = restaurantList.indexOf(tagMenu);
			int endResto = restaurantList.indexOf(tagMenuEnd);
			String restAttributes = restaurantList.substring(
					startResto + tagMenu.length(), endResto).trim();

			// Restaurant Feeds
			int startUrl = restAttributes.indexOf(tagUrl);
			int endUrl = restAttributes.indexOf(tagUrlEnd);
			String restaurantUrl = restAttributes.substring(
					startUrl + tagUrl.length(), endUrl).trim();

			feed = restaurantUrl;
			restaurantList = restaurantList.substring(
					endResto + tagMenuEnd.length(), restaurantList.length())
					.trim();
		}

		return feed;
	}
}