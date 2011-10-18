package org.pocketcampus.plugin.camipro.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface ICamiproView extends IView {
	void transactionsUpdated();
	void balanceUpdated();
	void ebankingUpdated();
}
