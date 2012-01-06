package org.pocketcampus.plugin.news.server.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

/**
 * Parses the contents of the Feeds file which is stored on the server.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedsListParser {

	/** The path to the file to parse. */
	private String mFile;

	/** The HashMap of feeds and their Url. */
	private HashMap<String, HashMap<String, String>> feedsForAllLanguages;

	/** The whole feeds file in one String. */
	private String feedString;

	/**
	 * Constructor for the NewsListParser.
	 * 
	 * @param file
	 *            The path to the file to parse.
	 * @throws IllegalArgumentException
	 *             if the file path is null
	 */
	public FeedsListParser(String file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		mFile = file;
		feedsForAllLanguages = new HashMap<String, HashMap<String, String>>();
		// Parse the first file
		HashMap<String, String> filesForLanguages = null;
		feedString = getContents(mFile);
		filesForLanguages = feedsLanguages(feedString);

		// Then for each of those found, do it again.
		Set<String> languages = filesForLanguages.keySet();
		for (String language : languages) {
			feedString = getContents(filesForLanguages.get(language));
			HashMap<String, String> feedsForLanguage = newsFeeds(feedString);
			feedsForAllLanguages.put(language, feedsForLanguage);
		}
	}

	/**
	 * @return The HashMap of Feed Names and their corresponding Url.
	 */
	public HashMap<String, HashMap<String, String>> getFeeds() {
		return feedsForAllLanguages;
	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 */
	private String getContents(String languageFile) {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			InputStream instream = this.getClass().getResourceAsStream(
					languageFile);

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
	 * Constructs the hashmap of Feed languages with their file containing the
	 * feed Urls.
	 * 
	 * @param feedList
	 *            The path to the file to parse.
	 * @return A HashMap of Feed names with their corresponding Urls.
	 */
	private HashMap<String, String> feedsLanguages(String languagesFile) {
		String tagLanguageFile = "<NewsLanguageFile>";
		String tagLanguage = "<Language>";
		String tagFile = "<File>";

		String tagLanguageFileEnd = "</NewsLanguageFile>";
		String tagLanguageEnd = "</Language>";
		String tagFileEnd = "</File>";

		HashMap<String, String> feeds = new HashMap<String, String>();

		while (languagesFile.length() > 1) {
			// News Feeds
			int startFeed = languagesFile.indexOf(tagLanguageFile);
			int endFeed = languagesFile.indexOf(tagLanguageFileEnd);
			String restAttributes = languagesFile.substring(
					startFeed + tagLanguageFile.length(), endFeed).trim();

			// News Feeds Language
			int startLanguage = restAttributes.indexOf(tagLanguage);
			int endName = restAttributes.indexOf(tagLanguageEnd);
			String feedLanguage = restAttributes.substring(
					startLanguage + tagLanguage.length(), endName).trim();

			// News Feeds File
			int startFile = restAttributes.indexOf(tagFile);
			int endFile = restAttributes.indexOf(tagFileEnd);
			String feedFile = restAttributes.substring(
					startFile + tagFile.length(), endFile).trim();

			System.out.println(feedLanguage + " " + feedFile);

			feeds.put(feedLanguage, feedFile);
			languagesFile = languagesFile.substring(
					endFeed + tagLanguageFileEnd.length(),
					languagesFile.length()).trim();
		}

		return feeds;
	}

	/**
	 * Constructs the hashmap of Feed names with their Urls.
	 * 
	 * @param feedList
	 *            The path to the file to parse.
	 * @return A HashMap of Feed names with their corresponding Urls.
	 */
	private HashMap<String, String> newsFeeds(String feedList) {
		String tagFeed = "<Feed>";
		String tagUrl = "<rssfeed>";
		String tagName = "<name>";

		String tagNameEnd = "</name>";
		String tagFeedEnd = "</Feed>";
		String tagUrlEnd = "</rssfeed>";

		HashMap<String, String> feeds = new HashMap<String, String>();

		while (feedList.length() > 1) {
			// Feed
			int startFeed = feedList.indexOf(tagFeed);
			int endFeed = feedList.indexOf(tagFeedEnd);
			String restAttributes = feedList.substring(
					startFeed + tagFeed.length(), endFeed).trim();

			// Feed Names
			int startName = restAttributes.indexOf(tagName);
			int endName = restAttributes.indexOf(tagNameEnd);
			String feedName = restAttributes.substring(
					startName + tagName.length(), endName).trim();

			// Feed Urls
			int startUrl = restAttributes.indexOf(tagUrl);
			int endUrl = restAttributes.indexOf(tagUrlEnd);
			String feedUrl = restAttributes.substring(
					startUrl + tagUrl.length(), endUrl).trim();

			feeds.put(feedName, feedUrl);
			feedList = feedList.substring(endFeed + tagFeedEnd.length(),
					feedList.length()).trim();
		}

		return feeds;
	}
}