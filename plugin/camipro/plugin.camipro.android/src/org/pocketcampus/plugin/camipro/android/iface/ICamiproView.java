package org.pocketcampus.plugin.camipro.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface ICamiproView extends IView {
	void transactionsUpdated();
	void balanceUpdated();
	void cardLoadingWithEbankingInfoUpdated();
	void cardStatisticsUpdated();
	void lastUpdateDateUpdated();
	
	void networkErrorHappened();
	void camiproServersDown();
	void notLoggedIn();
	void emailSent(String result);
}
