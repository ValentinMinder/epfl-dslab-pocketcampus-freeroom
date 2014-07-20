package org.pocketcampus.plugin.food.android;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.food.R;
import org.pocketcampus.plugin.food.android.iface.IFoodController;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.android.req.CastVoteRequest;
import org.pocketcampus.plugin.food.android.req.GetFoodRequest;
import org.pocketcampus.plugin.food.shared.EpflMeal;
import org.pocketcampus.plugin.food.shared.EpflRating;
import org.pocketcampus.plugin.food.shared.EpflRestaurant;
import org.pocketcampus.plugin.food.shared.FoodRequest;
import org.pocketcampus.plugin.food.shared.MealTime;
import org.pocketcampus.plugin.food.shared.MealType;
import org.pocketcampus.plugin.food.shared.PriceTarget;
import org.pocketcampus.plugin.food.shared.FoodService.Client;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.VoteRequest;
import org.pocketcampus.plugin.map.shared.MapItem;

import android.provider.Settings.Secure;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


/**
 * Controller for the food plugin. Takes care of interactions between the model
 * and the view and gets information from the server.
 * 
 * @author Amer <amer@accandme.com>
 */
public class FoodController extends PluginController implements IFoodController {

	/** The plugin's model. */
	private FoodModel mModel;

	/** Interface to the plugin's server client */
	private Iface mClient;

	/** The name of the plugin */
	private String mPluginName = "food";
	
	/**
	 * Some Private Vars
	 */
	private String androidId;
	private Map<MealType, String> iMealTypePicUrls;
	private Map<Long, AResto> iRestos;

	private Map<Long, String> iRestoNames;
	private Map<MealType, String> iTypeNames;

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new FoodModel(getApplicationContext());

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		
		androidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	
	

	/**
	 * Setter and getter for iMealTypePicUrls, iRestos, iRestoNames, iTypeNames
	 */
	public Map<MealType, String> getMealTypePicUrls() {
		return iMealTypePicUrls;
	}
	public void setMealTypePicUrls(Map<MealType, String> obj) {
		iMealTypePicUrls = obj;
	}
	public Map<Long, AResto> getRestos() {
		return iRestos;
	}
	public void setRestos(Map<Long, AResto> obj) {
		iRestos = obj;
	}
	public Map<Long, String> getRestoNames() {
		return iRestoNames;
	}
	public void setRestoNames(Map<Long, String> obj) {
		iRestoNames = obj;
	}
	public Map<MealType, String> getTypeNames() {
		return iTypeNames;
	}
	public void setTypeNames(Map<MealType, String> obj) {
		iTypeNames = obj;
	}

	
	
	public void setEpflMenus(List<EpflRestaurant> menus, PriceTarget priceTarget) {
		
//		System.out.println("setEpflMenus # of resto " + menus.size());
		
		if(mModel.getUserStatus() != null)
			priceTarget = mModel.getUserStatus();

		Map<Long, AResto> restos = new HashMap<Long, AResto>();
		Map<Long, AMeal> meals = new HashMap<Long, AMeal>();
		
		for(EpflRestaurant r : menus) {
//			System.out.println("# of resto's meals " + r.getRMealsSize());
			restos.put(r.getRId(), new AResto(r.getRId(), r.getRName(), getSatisfaction(r.getRRating()), r.getRLocation()));
			for(EpflMeal m : r.getRMeals()) {
				Double price = m.getMPrices().get(priceTarget != null ? priceTarget : PriceTarget.VISITOR);
				if(price == null) price = m.getMPrices().get(PriceTarget.ALL);
				meals.put(m.getMId(), new AMeal(m.getMId(), m.getMName(), getDescription(m.getMDescription()), getPrice(price), getSatisfaction(m.getMRating()), m.getMTypes(), r.getRId()));
			}
		}
		
		Map<Long, String> restoNames = new HashMap<Long, String>();
		Map<MealType, String> typeNames = new HashMap<MealType, String>();
		for(AResto r : restos.values())
			restoNames.put(r.id, r.name);
		for(MealType t : MealType.values())
			typeNames.put(t, translateEnum(t.name()));
		setRestoNames(restoNames);
		setTypeNames(typeNames);
		
		setRestos(restos);
		mModel.setMeals(meals);
	}
	
	
	
	

	/**
	 * Initiates a request to the server to get food.
	 */
	public void refreshFood(IFoodView caller, Long foodDay, MealTime foodTime, boolean useCache) {
		FoodRequest req = new FoodRequest();
		req.setDeviceLanguage(Locale.getDefault().getLanguage());
		if(foodDay != null)
			req.setMealDate(foodDay);
		if(foodTime != null)
			req.setMealTime(foodTime);
		new GetFoodRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}

	/**
	 * Initiates a request to vote.
	 */
	public void sendVoteReq(IFoodView caller, long mealId, double rating) {
		VoteRequest req = new VoteRequest(mealId, rating, androidId);
		new CastVoteRequest(caller).start(this, mClient, req);
	}

	/**
	 * HELPERS CLASSES
	 *
	 */
	
	public static class AResto {
		public AResto(long id, String name, String satisfaction, MapItem location) {
			this.id = id;
			this.name = name;
			this.satisfaction = satisfaction;
			this.location = location;
		}
		long id;
		String name;
		String satisfaction;
		MapItem location;
	}
	public static class AMeal {
		public AMeal(long id, String name, String desc, String price, String satisfaction, List<MealType> types, long resto) {
			this.id = id;
			this.name = name;
			this.desc = desc;
			this.price = price;
			this.satisfaction = satisfaction;
			this.types = types;
			this.resto = resto;
		}
		long id;
		String name;
		String desc;
		String price;
		String satisfaction;
		List<MealType> types;
		long resto;
	}
	
	private String getSatisfaction(EpflRating rating) {
		if(rating.getVoteCount() == 0) 
			return getString(R.string.food_text_novotes);
		String satiscation = Math.round(rating.getRatingValue() * 100) + "% " + getString(R.string.food_text_satisfaction);
		String votes = rating.getVoteCount() + " " + getString(rating.getVoteCount() > 1 ? R.string.food_text_votes : R.string.food_text_vote);
		return satiscation + " (" + votes + ")";
	}
	private String getDescription(String desc) {
		desc = desc.trim();
		if(desc.length() == 0)
			return null;
		return desc.replaceAll("\\n", "<br>");
	}
	private String getPrice(Double d) {
		return (d == null ? null : (String.format("%1$.2f", d) + "<br>CHF"));
	}

	
	public String translateEnum (String enumVal) {
		int resId = getResources().getIdentifier("food_enum_" + enumVal, "string", getPackageName());
		if(resId == 0) return enumVal;
		return getString(resId);
	}

}
