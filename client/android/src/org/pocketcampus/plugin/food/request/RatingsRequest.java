package org.pocketcampus.plugin.food.request;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.shared.plugin.food.Rating;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public abstract class RatingsRequest extends DataRequest {
	private HashMap<Integer, Rating> campusMenuRatingsList;

	@Override
	protected void doInUiThread(String result) {
		campusMenuRatingsList = new HashMap<Integer, Rating>();
		if (result != null) {
			Log.d("SERVER", result);
		}
		
		Type menuType = new TypeToken<HashMap<Integer, Rating>>() {
		}.getType();
		try {
			campusMenuRatingsList = Json.fromJson(result, menuType);
		} catch (JsonSyntaxException e) {
			Log.d("SERVER", "Jsonsyntax");
			e.printStackTrace();
			cancel(true);
			return;
		} catch (JsonException e) {
			e.printStackTrace();
			cancel(true);
			return;
		}
		updateRatings(campusMenuRatingsList);
	}
	public abstract void updateRatings(HashMap<Integer, Rating> ratings);
}