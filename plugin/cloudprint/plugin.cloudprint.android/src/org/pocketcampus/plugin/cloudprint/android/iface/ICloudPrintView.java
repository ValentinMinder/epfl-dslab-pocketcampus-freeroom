package org.pocketcampus.plugin.cloudprint.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * ICloudPrintView
 * 
 * Interface for the Views of the CloudPrint plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface ICloudPrintView extends IView {
	
	

	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void notLoggedIn();
	void printServerError();
	void uploadComplete(long jobId);	
	void printedSuccessfully();
	void printPreviewReady(int pageCount);


	/**
	 * Authentication callbacks.
	 * Called FROM Controller on ALL listeners because we dunno who is instantiated 
	 */
	void authenticationFinished();
	void authenticationFailed();
	void userCancelledAuthentication();
	
	
	
}
