package org.pocketcampus.plugin.bikes.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesView;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

public class BikesModel extends PluginModel implements IBikesModel{
	IBikesView mListeners = (IBikesView) getListeners();
	
	private List<BikeEmplacement> bikeList;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return IBikesView.class;
	}

	public void setResults(List<BikeEmplacement> result) {
		bikeList = result;
		mListeners.bikeListUpdated();
		
	}

	@Override
	public List<BikeEmplacement> getAvailablesBikes() {
		if(bikeList == null)
			return new ArrayList<BikeEmplacement>();
		else
			return bikeList;
	}

}
