package org.pocketcampus.plugin.camipro.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * ICamiproView
 * 
 * Interface for the Views of the Camipro plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface ICamiproView extends IView {
	
	/**
	 * Update display when we have data.
	 */
	void transactionsUpdated();
	void balanceUpdated();
	void cardLoadingWithEbankingInfoUpdated();
	void cardStatisticsUpdated();
	void lastUpdateDateUpdated();
	
	/**
	 * Authentication callbacks.
	 */
	void gotCamiproCookie();
	void authenticationFailed();
	void userCancelledAuthentication();
	
	/**
	 * Display errors and notices.
	 */
	void networkErrorHappened();
	void camiproServersDown();
	void emailSent(String result);
	
}
