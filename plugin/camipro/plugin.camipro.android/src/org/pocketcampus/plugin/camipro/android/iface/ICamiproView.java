package org.pocketcampus.plugin.camipro.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

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
	 * Methods called by the Model.
	 */
	void transactionsUpdated();
	void balanceUpdated();
	void cardLoadingWithEbankingInfoUpdated();
	void cardStatisticsUpdated();
	void lastUpdateDateUpdated();
	void tequilaTokenUpdated();
	void camiproCookieUpdated();
	
	void tokenAuthenticationFinished();
	
	/**
	 * Methods called by the "Request" classes.
	 */
	void networkErrorHappened();
	void camiproServersDown();
	void notLoggedIn();
	void emailSent(String result);
	
}
