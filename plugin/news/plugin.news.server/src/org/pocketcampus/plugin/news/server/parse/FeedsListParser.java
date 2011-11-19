package org.pocketcampus.plugin.news.server.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the contents of the Feeds file which is stored on the server
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedsListParser {

	/** The path to the file to parse */
	private String mFile;

	/** The list of feeds */
	private List<String> feeds;

	/** The whole feeds file in one String */
	private String feedString;

	/**
	 * Constructor for the RestaurantListParser
	 * 
	 * @param file
	 * @throws IllegalArgumentException
	 *             if the file path is null
	 */
	public FeedsListParser(String file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		mFile = file;
		feedString = getContents();
		feeds = newsFeeds(feedString);
	}

	/** Returns the list of Strings and their corresponding Url */
	public List<String> getFeeds() {
		return feeds;
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
	 * Constructs the list of Feed Urls
	 * 
	 * @param feedList
	 *            the path to the file to parse
	 * @return a list of Feed Urls
	 */
	private List<String> newsFeeds(String feedList) {
		String tagFeed = "<Feed>";
		String tagUrl = "<rssfeed>";

		String tagRestEnd = "</Feed>";
		String tagUrlEnd = "</rssfeed>";

		List<String> feeds = new ArrayList<String>();

		while (feedList.length() > 1) {
			// Feed
			int startFeed = feedList.indexOf(tagFeed);
			int endResto = feedList.indexOf(tagRestEnd);
			String restAttributes = feedList.substring(
					startFeed + tagFeed.length(), endResto).trim();

			// Feed Urls
			int startUrl = restAttributes.indexOf(tagUrl);
			int endUrl = restAttributes.indexOf(tagUrlEnd);
			String restaurantUrl = restAttributes.substring(
					startUrl + tagUrl.length(), endUrl).trim();

			feeds.add(restaurantUrl);
			feedList = feedList.substring(endResto + tagRestEnd.length(),
					feedList.length()).trim();
		}

		return feeds;
	}
}