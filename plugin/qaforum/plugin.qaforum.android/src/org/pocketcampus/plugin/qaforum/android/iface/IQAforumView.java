package org.pocketcampus.plugin.qaforum.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * IQAforumView
 * 
 * Interface for the Views of the QAforum plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 *
 */
public interface IQAforumView extends IView {
	
	void userCancelledAuthentication();
	void authenticationFailed();
	
	void gotRequestReturn();
	void loadingFinished();
	void messageDeleted();
	
}
