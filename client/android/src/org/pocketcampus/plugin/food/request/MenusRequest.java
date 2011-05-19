package org.pocketcampus.plugin.food.request;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.shared.plugin.food.Meal;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public abstract class MenusRequest extends DataRequest {
	private List<Meal> campusMenuList;

	@Override
	protected void doInUiThread(String result) {
		campusMenuList = new ArrayList<Meal>();
		Log.d("SERVER", result);
		
		// Deserializes the response
		Type menuType = new TypeToken<List<Meal>>() {
		}.getType();
		try {
			campusMenuList = Json.fromJson(result, menuType);
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
		if(campusMenuList != null){
			Log.d("SERVER", "Size : "+campusMenuList.size());
			updateMenus(campusMenuList);
		}
	}
	
	public abstract void updateMenus(List<Meal> menus);
}