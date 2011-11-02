package org.pocketcampus.plugin.camipro.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.EbankingBean;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class CamiproModel extends PluginModel implements ICamiproModel {
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICamiproView.class;
	}

	@Override
	public List<Transaction> getTransactions() {
		return transactionsList;
	}

	@Override
	public Double getBalance() {
		return balanceValue;
	}

	@Override
	public EbankingBean getEbanking() {
		return ebankingBean;
	}
	
	public void setTransactions(List<Transaction> trans) {
		transactionsList = trans;
		mListeners.transactionsUpdated();
	}

	public void setBalance(Double bal) {
		balanceValue = bal;
		mListeners.balanceUpdated();
	}

	public void setEbanking(EbankingBean ebank) {
		ebankingBean = ebank;
		mListeners.ebankingUpdated();
	}
	
	ICamiproView mListeners = (ICamiproView) getListeners();
	
	private List<Transaction> transactionsList;
	private Double balanceValue;
	private EbankingBean ebankingBean;

}
