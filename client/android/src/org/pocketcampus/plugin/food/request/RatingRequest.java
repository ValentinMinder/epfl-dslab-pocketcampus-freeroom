package org.pocketcampus.plugin.food.request;

import java.lang.reflect.Type;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.shared.plugin.food.Rating;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public abstract class RatingRequest extends DataRequest {
	private Rating newRating;

	@Override
	protected void doInUiThread(String result) {
		newRating = new Rating();
		
		Log.d("SERVER", result == null ? "Result null" : "Result ok");
		
		Type ratingType = new TypeToken<Rating>() {
		}.getType();
		try {
			newRating = Json.fromJson(result, ratingType);
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
		updateRating(newRating);
	}
	public abstract void updateRating(Rating newRating);
}