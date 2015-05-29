package org.pocketcampus.plugin.food.android;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.MealType;
import org.pocketcampus.plugin.food.shared.PriceTarget;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * The Model of the food plugin, used to handle the information that is going to
 * be displayed in the views
 * 
 * @author Amer <amer@accandme.com>
 * 
 */
public class FoodModel extends PluginModel implements IFoodModel {
	
	/**
	 * Some constants.
	 */
	private static final String FOOD_STORAGE_NAME = "FOOD_STORAGE_NAME";
	private static final String FOOD_DISLIKED_RESTOS_KEY = "FOOD_DISLIKED_RESTOS_KEY";
	private static final String FOOD_DISLIKED_TYPES_KEY = "FOOD_DISLIKED_TYPES_KEY";
	private static final String FOOD_EXPANDED_RESTOS_KEY = "FOOD_EXPANDED_RESTOS_KEY";
	private static final String FOOD_USER_STATUS_KEY = "FOOD_USER_STATUS_KEY";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IFoodView mListeners = (IFoodView) getListeners();
	
	/**
	 * Member variables that need to be persistent
	 */
	private Set<Long> dislikedRestos = new HashSet<Long>();
	private Set<MealType> dislikedTypes = new HashSet<MealType>();
	private Set<Long> expandedRestos = new HashSet<Long>();
	private PriceTarget userStatus = null;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public FoodModel(Context context) {
		iStorage = context.getSharedPreferences(FOOD_STORAGE_NAME, 0);
		
		dislikedRestos = decodeRestos(iStorage.getString(FOOD_DISLIKED_RESTOS_KEY, ""));
		dislikedTypes = decodeTypes(iStorage.getString(FOOD_DISLIKED_TYPES_KEY, ""));
		expandedRestos = decodeRestos(iStorage.getString(FOOD_EXPANDED_RESTOS_KEY, ""));
		int userStatusInt = iStorage.getInt(FOOD_USER_STATUS_KEY, 0);
		userStatus = (userStatusInt == 0 ? null : PriceTarget.findByValue(userStatusInt));
		
	}
	
	
	/**
	 * Setter and getter for persistent stuff;
	 */
	public void addDislikedResto(Long resto) {
		dislikedRestos.add(resto);
		savePrefs();
	}
	public void removeDislikedResto(Long resto) {
		dislikedRestos.remove(resto);
		savePrefs();
	}
	public Set<Long> getDislikedRestos() {
		return dislikedRestos;
	}
	public void addDislikedType(MealType type) {
		dislikedTypes.add(type);
		savePrefs();
	}
	public void removeDislikedType(MealType type) {
		dislikedTypes.remove(type);
		savePrefs();
	}
	public Set<MealType> getDislikedTypes() {
		return dislikedTypes;
	}
	public void addExpandedResto(Long resto) {
		expandedRestos.add(resto);
		savePrefs();
	}
	public void removeExpandedResto(Long resto) {
		expandedRestos.remove(resto);
		savePrefs();
	}
	public Set<Long> getExpandedRestos() {
		return expandedRestos;
	}
	public void setUserStatus(PriceTarget target) {
		userStatus = target;
		savePrefs();
	}
	public PriceTarget getUserStatus() {
		return userStatus;
	}
	
	
	private void savePrefs() {
		iStorage.edit()
				.putString(FOOD_DISLIKED_RESTOS_KEY, encodeRestos(dislikedRestos))
				.putString(FOOD_DISLIKED_TYPES_KEY, encodeTypes(dislikedTypes))
				.putString(FOOD_EXPANDED_RESTOS_KEY, encodeRestos(expandedRestos))
				.putInt(FOOD_USER_STATUS_KEY, (userStatus == null ? 0 : userStatus.getValue()))
				.commit();
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFoodView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IFoodView getListenersToNotify() {
		return mListeners;
	}
	
	
	/***
	 * HELPER FUNCS
	 */

	private String encodeRestos(Set<Long> restos) {
		return TextUtils.join(",", restos);
	}
	private Set<Long> decodeRestos(String restos) {
		Set<Long> decoded = new HashSet<Long>();
		if("".equals(restos))
			return decoded;
		for(String s : restos.split("[,]")) {
			decoded.add(Long.parseLong(s));
		}
		return decoded;
	}
	private String encodeTypes(Set<MealType> types) {
		List<String> str = new LinkedList<String>();
		for(MealType t : types) {
			str.add(t.getValue() + "");
		}
		return TextUtils.join(",", str);
	}
	private Set<MealType> decodeTypes(String types) {
		Set<MealType> decoded = new HashSet<MealType>();
		if("".equals(types))
			return decoded;
		for(String s : types.split("[,]")) {
			decoded.add(MealType.findByValue(Integer.parseInt(s)));
		}
		return decoded;
	}
	
	
}
