package org.pocketcampus.plugin.myedu.android;


import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.myedu.android.iface.IMyEduModel;
import org.pocketcampus.plugin.myedu.android.iface.IMyEduView;

import android.content.Context;

/**
 * MyEduModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the MyEdu plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.myeduCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class MyEduModel extends PluginModel implements IMyEduModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IMyEduView mListeners = (IMyEduView) getListeners();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public MyEduModel(Context context) {
		
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IMyEduView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IMyEduView getListenersToNotify() {
		return mListeners;
	}
	
}
