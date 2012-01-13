package org.pocketcampus.plugin.bikes.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesView;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

/**
 * The model for the Bikes plugin, contains the data to be displayed 
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class BikesModel extends PluginModel implements IBikesModel{
	/**Listeners to this model*/
	IBikesView mListeners = (IBikesView) getListeners();
	
	/** List of <code>BikeEmplacement</code> obtained from the server*/
	private List<BikeEmplacement> bikeList;

	/**
	 * Returns the interface of the linked view
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IBikesView.class;
	}

	/**
	 * Setter for the result of a request.
	 * Notifies the listeners that the list has been updated.
	 * @param result List of <code>BikeEmplacement</code>
	 */
	public void setResults(List<BikeEmplacement> result) {
		bikeList = result;
		mListeners.bikeListUpdated();
	}

	/**
	 * Getter for the results of the request.
	 * If the result is not set yet, returns a empty list.
	 */
	@Override
	public List<BikeEmplacement> getAvailablesBikes() {
		if(bikeList == null)
			return new ArrayList<BikeEmplacement>();
		else
			return bikeList;
	}

}
