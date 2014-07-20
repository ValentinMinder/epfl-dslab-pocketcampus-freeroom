package org.pocketcampus.plugin.pushnotif.android;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifView;

import android.content.Context;

/**
 * PushNotifModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the PushNotif plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.pushnotifCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class PushNotifModel extends PluginModel implements IPushNotifModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IPushNotifView mListeners = (IPushNotifView) getListeners();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public PushNotifModel(Context context) {
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IPushNotifView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IPushNotifView getListenersToNotify() {
		return mListeners;
	}
	
}
