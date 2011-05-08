package org.pocketcampus.plugin.food.sandwiches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.util.Log;
import android.widget.Toast;

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
		if(sandwichList_ == null || sandwichList_.isEmpty()){
			Log.d("SANDWICHES", "Reloading Sandwiches");
			loadSandwiches();
		}
		return sandwichList_;
	}

	public void refreshSandwiches() {
		Log.d("SERVER", "Refreshing.");
		if (sandwichList_.isEmpty()) {
			Log.d("SERVER", "Reloading sandwiches");
			loadSandwiches();
		}
	}
	
	public boolean isEmpty(){
		return sandwichList_.isEmpty();
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
				writeToFile();
				Log.d("SANDWICHES", "Writing to file");
				pluginHandler_.menuRefreshed(true);
			}
		}
		sandwichList_ = restoreFromFile();
		if(sandwichList_ == null || sandwichList_.isEmpty()){						
			Log.d("SANDWICHES", "Requesting sandwiches.");
			FoodPlugin.getFoodRequestHandler().execute(new SandwichRequest(),
					"getSandwiches", (RequestParameters) null);
		}else{
			pluginHandler_.menuRefreshed(true);
		}
		
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
	
	
	public void writeToFile() {
		String filename = "SandwichCache";
		File sandwichFile = new File(pluginHandler_.getCacheDir(), filename);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(sandwichFile);
			out = new ObjectOutputStream(fos);

			out.writeObject(sandwichList_);
			out.close();
		} catch (IOException ex) {
			Toast.makeText(pluginHandler_, "Writing IO Exception", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Vector<Sandwich>> restoreFromFile() {
		String filename = "SandwichCache";
		HashMap<String, Vector<Sandwich>> sandwiches = null;
		File toGet = new File(pluginHandler_.getCacheDir(), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);

			sandwiches = (HashMap<String, Vector<Sandwich>>) in.readObject();

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}

		return sandwiches;
	}
}
