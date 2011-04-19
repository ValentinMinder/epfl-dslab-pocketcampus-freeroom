package org.pocketcampus.plugin.food.sandwiches;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SandwichList {

	private Vector<Vector<Sandwich>> sandwichList_;
	private List<Sandwich> sandwichFromServer_;
	
	public SandwichList(){
		loadSandwiches();
	}
	
	public Vector<Vector<Sandwich>> getStoreList() {
		return sandwichList_;
	}
	
	private void loadSandwiches(){
		class SandwichRequest extends DataRequest {
			
			@Override
			protected void doInUiThread(String result) {

				sandwichList_ = new Vector<Vector<Sandwich>>();
				
				sandwichFromServer_ = new ArrayList<Sandwich>();
				
				if(result != null){
					Log.d("SANDWICHES", result);
				} else {
					Log.d("SANDWICHES", "null");
				}
				
				// Deserializes the response
				Gson gson = new Gson();

				Type menuType = new TypeToken<List<Sandwich>>() {}.getType();
				try {
					sandwichFromServer_ = gson.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					Log.d("SANDWICHES", "Jsonsyntax");
					e.printStackTrace();
					return;
				} catch (Exception e){
					e.printStackTrace();
					Log.d("SANDWICHES","Json Exception !");
				}
			}
		}

		FoodPlugin.getFoodRequestHandler().execute(new SandwichRequest(), "getSandwiches",
				(RequestParameters) null);
	}
		
}
