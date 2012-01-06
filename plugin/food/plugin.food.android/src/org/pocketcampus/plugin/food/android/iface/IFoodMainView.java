package org.pocketcampus.plugin.food.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

/**
 * The interface that defines the public methods of FoodMainView.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public interface IFoodMainView extends IView {

	/**
	 * Called when the list of restaurants has been updated from the server.
	 */
	public void restaurantsUpdated();

	/**
	 * Called when the list of meals has been updated from the server.
	 */
	public void menusUpdated();

	/**
	 * Called when the list of ratings has been updated from the server.
	 */
	public void ratingsUpdated();

	/**
	 * Called when the user has voted for an item and it has been submitted to
	 * the server.
	 * 
	 * @param status
	 *            the status the server returned for the rating submission.
	 */
	public void ratingSubmitted(SubmitStatus status);

	/**
	 * Called when an error happens while executing a request to the server.
	 * 
	 * @param message
	 *            the message to be displayed.
	 */
	public void networkErrorHappened(String message);

}
