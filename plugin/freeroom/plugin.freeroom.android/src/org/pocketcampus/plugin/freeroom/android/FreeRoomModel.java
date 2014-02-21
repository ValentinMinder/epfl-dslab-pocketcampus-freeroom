package org.pocketcampus.plugin.freeroom.android;


import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.content.Context;

/**
 * FreeRoomModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the FreeRoom plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.freeroomCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FreeRoomModel extends PluginModel implements IFreeRoomModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IFreeRoomView mListeners = (IFreeRoomView) getListeners();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public FreeRoomModel(Context context) {
		
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFreeRoomView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IFreeRoomView getListenersToNotify() {
		return mListeners;
	}
	
}
