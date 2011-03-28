package org.pocketcampus.plugin.food.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.pocketcampus.R;

import android.content.Context;

public class RestaurantListParser {

	private HashMap<String, String> feeds;
	private Context ctx_;
	
	RestaurantListParser(Context context){
		ctx_ = context;
//		File testFile = new File("restaurants_list.txt");
		feeds = restaurantFeeds(getContents());
	}
	
	public HashMap<String, String> getFeeds(){
		return feeds;
	}
	
	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 * @param aFile
	 *            is a file which already exists and can be read.
	 */
	private String getContents() {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			InputStream instream = ctx_.getResources().openRawResource(
					R.raw.restaurants_list);
//			InputStream instream = ctx_.openFileInput("restaurants_list.txt");
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
	
	private HashMap<String, String> restaurantFeeds(String restaurantList){
		String tagRestaurant = "<Resto>";
		String tagName = "<name>";
		String tagUrl = "<rssfeed>";
		
		String tagRestEnd = "</Resto>";
		String tagNameEnd = "</name>";
		String tagUrlEnd = "</rssfeed>";
		
		HashMap<String, String> feeds = new HashMap<String, String>();
		
		while(restaurantList.length()>1){
			//Restaurant
			int startResto = restaurantList.indexOf(tagRestaurant);
			int endResto = restaurantList.indexOf(tagRestEnd);
			String restAttributes = restaurantList.substring(startResto+tagRestaurant.length(), endResto).trim();
			
			//Restaurant Names
			int startName = restAttributes.indexOf(tagName);
			int endName = restAttributes.indexOf(tagNameEnd);
			String restaurantName = restAttributes.substring(startName+tagName.length(), endName).trim();
			
			//Restaurant Feeds
			int startUrl = restAttributes.indexOf(tagUrl);
			int endUrl = restAttributes.indexOf(tagUrlEnd);
			String restaurantUrl = restAttributes.substring(startUrl+tagUrl.length(), endUrl).trim();
			
			feeds.put(restaurantName, restaurantUrl);
			
			restaurantList = restaurantList.substring(endResto+tagRestEnd.length(), restaurantList.length()).trim();
		}
		
		return feeds;
	}
}