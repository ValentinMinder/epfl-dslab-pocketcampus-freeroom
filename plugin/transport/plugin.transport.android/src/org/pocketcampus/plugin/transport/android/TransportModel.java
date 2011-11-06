package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Location;

public class TransportModel extends PluginModel {
	private List<Location> mPreferredDestinations;
	private ITransportView mListeners = (ITransportView) getListeners();
	
	public TransportModel() {
		mPreferredDestinations = new ArrayList<Location>();
	}
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITransportView.class;
	}

	public List<Location> getPreferredDestinations() {
		return mPreferredDestinations;
	}

	public void setPreferredDestinations(List<Location> destinations) {
		mPreferredDestinations.clear();
		mPreferredDestinations.addAll(destinations);
		mListeners.preferredDestinationsUpdated();
	}
	
}
