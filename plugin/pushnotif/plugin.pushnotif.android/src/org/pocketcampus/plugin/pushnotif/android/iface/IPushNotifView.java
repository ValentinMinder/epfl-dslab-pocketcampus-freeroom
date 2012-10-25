package org.pocketcampus.plugin.pushnotif.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * IPushNotifView
 * 
 * Interface for the Views of the PushNotif plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IPushNotifView extends IView {
		
	/**
	 * Display errors and notices.
	 */
	void networkErrorHappened();
	
}
