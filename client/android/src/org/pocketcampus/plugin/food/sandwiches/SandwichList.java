package org.pocketcampus.plugin.food.sandwiches;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SandwichList {
	private FoodPlugin pluginHandler_;
	private HashMap<String, Vector<Sandwich>> sandwichList_;
	private List<Sandwich> sandwichFromServer_;

	public SandwichList(FoodPlugin ownerActivity) {
		pluginHandler_ = ownerActivity;
		loadSandwiches();
	}

	public HashMap<String, Vector<Sandwich>> getStoreList() {
		return sandwichList_;
	}

	public void refreshSandwiches() {
		Log.d("SERVER", "Refreshing.");
		if (sandwichList_.isEmpty()) {
			Log.d("SERVER", "Reloading sandwiches");
			loadSandwiches();
		}
	}

	private void loadSandwiches() {
		pluginHandler_.menuRefreshing();

		class SandwichRequest extends DataRequest {

			@Override
			public void onCancelled() {
				Log.d("SANDWICHES", "Task cancelled");
				pluginHandler_.menuRefreshed(false);
			}

			@Override
			protected void doInUiThread(String result) {

				sandwichFromServer_ = new ArrayList<Sandwich>();

				if (result != null) {
					Log.d("SANDWICHES", result);
				} else {
					Log.d("SANDWICHES", "null");
				}

				// Deserializes the response
				Gson gson = new Gson();

				Type menuType = new TypeToken<List<Sandwich>>() {
				}.getType();
				try {
					sandwichFromServer_ = gson.fromJson(result, menuType);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("SANDWICHES", "Json Exception !");
				}

				sandwichList_ = new HashMap<String, Vector<Sandwich>>();
				if (sandwichFromServer_ != null) {
					sandwichList_ = sortByRestaurant(sandwichFromServer_);
				}

				pluginHandler_.menuRefreshed(true);
			}
		}
		Log.d("SANDWICHES", "Requesting sandwiches.");
		FoodPlugin.getFoodRequestHandler().execute(new SandwichRequest(),
				"getSandwiches", (RequestParameters) null);
	}

	private HashMap<String, Vector<Sandwich>> sortByRestaurant(
			List<Sandwich> serverList) {
		HashMap<String, Vector<Sandwich>> hashMap = new HashMap<String, Vector<Sandwich>>();
		if (serverList != null) {
			for (Sandwich s : serverList) {
				if (hashMap.containsKey(s.getRestaurant())) {
					hashMap.get(s.getRestaurant()).add(s);
				} else {
					Vector<Sandwich> v = new Vector<Sandwich>();
					v.add(s);
					hashMap.put(s.getRestaurant(), v);
				}
			}
		}
		return hashMap;
	}

}
