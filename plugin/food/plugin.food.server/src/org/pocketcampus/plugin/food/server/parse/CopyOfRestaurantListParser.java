//package org.pocketcampus.plugin.food.server.parse;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Parses the contents of the Restaurant file which is stored on the server
// * 
// * @author Elodie <elodienilane.triponez@epfl.ch>
// * @author Oriane <oriane.rodriguez@epfl.ch>
// * 
// */
//public class RestaurantListParser {
//
//	/** The path to the file to parse */
//	private String mFile;
//
//	/** The list of feeds */
//	private HashMap<String, String> feeds;
//
//	/** The whole restaurant file in one String */
//	private String feedString;
//
//	/** Constructor for the RestaurantListParser */
//	public RestaurantListParser(String file) {
//		mFile = file;
//		feedString = getContents();
//		feeds = restaurantFeeds(feedString);
//	}
//
//	/** Returns the list of Strings and their corresponding Url */
//	public HashMap<String, String> getFeeds() {
//		return feeds;
//	}
//
//	/** Returns the list of Restaurants */
//	public ArrayList<String> getRestaurants() {
//		ArrayList<String> list = new ArrayList<String>();
//		
//		for(String r : feeds.keySet()) {
//			list.add(r);
//		}
//		
//		return list;
//	}
//
//	/**
//	 * Fetch the entire contents of a text file, and return it in a String. This
//	 * style of implementation does not throw Exceptions to the caller.
//	 * 
//	 */
//	private String getContents() {
//		// ...checks on aFile are elided
//		StringBuilder contents = new StringBuilder();
//
//		try {
//			// use buffering, reading one line at a time
//			InputStream instream = this.getClass().getResourceAsStream(mFile);
//
//			InputStreamReader inputreader = new InputStreamReader(instream);
//			BufferedReader input = new BufferedReader(inputreader);
//
//			try {
//				String line = null; // not declared within while loop
//				/*
//				 * readLine is a bit quirky : it returns the content of a line
//				 * MINUS the newline. it returns null only for the END of the
//				 * stream. it returns an empty String if two newlines appear in
//				 * a row.
//				 */
//				while ((line = input.readLine()) != null) {
//					contents.append(line);
//				}
//			} finally {
//				input.close();
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//
//		return contents.toString();
//	}
//
//	/**
//	 * Constructs the list of restaurants and their corresponding feed url
//	 * 
//	 * @param restaurantList
//	 *            the path to the file to parse
//	 * @return a hashmap of Restaurant names and their String Url
//	 */
//	private HashMap<String, String> restaurantFeeds(String restaurantList) {
//		String tagRestaurant = "<Resto>";
//		String tagName = "<name>";
//		String tagUrl = "<rssfeed>";
//
//		String tagRestEnd = "</Resto>";
//		String tagNameEnd = "</name>";
//		String tagUrlEnd = "</rssfeed>";
//
//		HashMap<String, String> feeds = new HashMap<String, String>();
//
//		while (restaurantList.length() > 1) {
//			// Restaurant
//			int startResto = restaurantList.indexOf(tagRestaurant);
//			int endResto = restaurantList.indexOf(tagRestEnd);
//			String restAttributes = restaurantList.substring(
//					startResto + tagRestaurant.length(), endResto).trim();
//
//			// Restaurant Names
//			int startName = restAttributes.indexOf(tagName);
//			int endName = restAttributes.indexOf(tagNameEnd);
//			String restaurantName = restAttributes.substring(
//					startName + tagName.length(), endName).trim();
//
//			// Restaurant Feeds
//			int startUrl = restAttributes.indexOf(tagUrl);
//			int endUrl = restAttributes.indexOf(tagUrlEnd);
//			String restaurantUrl = restAttributes.substring(
//					startUrl + tagUrl.length(), endUrl).trim();
//
//			feeds.put(restaurantName, restaurantUrl);
//			restaurantList = restaurantList.substring(
//					endResto + tagRestEnd.length(), restaurantList.length())
//					.trim();
//		}
//
//		return feeds;
//	}
//}