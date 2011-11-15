package org.pocketcampus.plugin.camipro.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class CamiproModel extends PluginModel implements ICamiproModel {
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICamiproView.class;
	}

	@Override
	public List<Transaction> getTransactions() {
		return iTransactions;
	}

	@Override
	public Double getBalance() {
		return iBalance;
	}

	@Override
	public String getCamiproCookie() {
		return camiproCookie;
	}
	
	@Override
	public CardStatistics getCardStatistics() {
		return iCardStatistics;
	}

	@Override
	public CardLoadingWithEbankingInfo getCardLoadingWithEbankingInfo() {
		return iCardLoadingWithEbankingInfo;
	}

	public void setTransactions(List<Transaction> trans) {
		iTransactions = trans;
		mListeners.transactionsUpdated();
	}

	public void setBalance(Double bal) {
		iBalance = bal;
		mListeners.balanceUpdated();
	}

	public void setCardStatistics(CardStatistics val) {
		iCardStatistics = val;
		mListeners.cardStatisticsUpdated();
	}
	
	public void setCardLoadingWithEbankingInfo(CardLoadingWithEbankingInfo val) {
		iCardLoadingWithEbankingInfo = val;
		mListeners.cardLoadingWithEbankingInfoUpdated();
	}
	
	ICamiproView mListeners = (ICamiproView) getListeners();
	
	public void setCamiproCookie(String aCamiproCookie) {
		camiproCookie = aCamiproCookie;
	}
	
	//TODO have camipro cookie saved in storage
	private String camiproCookie;
	
	private List<Transaction> iTransactions;
	private Double iBalance;
	private CardStatistics iCardStatistics;
	private CardLoadingWithEbankingInfo iCardLoadingWithEbankingInfo;
	
}
