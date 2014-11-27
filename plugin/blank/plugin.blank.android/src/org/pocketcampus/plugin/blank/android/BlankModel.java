package org.pocketcampus.plugin.blank.android;


import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.blank.android.iface.IBlankModel;
import org.pocketcampus.plugin.blank.android.iface.IBlankView;

import android.content.Context;

/**
 * BlankModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Blank plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.blankCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class BlankModel extends PluginModel implements IBlankModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IBlankView mListeners = (IBlankView) getListeners();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public BlankModel(Context context) {
		
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IBlankView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IBlankView getListenersToNotify() {
		return mListeners;
	}
	
}
