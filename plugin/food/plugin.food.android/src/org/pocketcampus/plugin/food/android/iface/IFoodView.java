package org.pocketcampus.plugin.food.android.iface;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

/**
 * The interface that defines the public methods of FoodMainView.
 * 
 * @author Amer <amer@accandme.com>
 */
public interface IFoodView extends IView {

	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void foodUpdated();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void foodServersDown();
	void voteCastFinished(SubmitStatus status);

	
}
