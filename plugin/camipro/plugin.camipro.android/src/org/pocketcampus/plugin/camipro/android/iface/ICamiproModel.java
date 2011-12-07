package org.pocketcampus.plugin.camipro.android.iface;

import java.util.List;

import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public interface ICamiproModel {
	public List<Transaction> getTransactions();
	public Double getBalance();
	public CardStatistics getCardStatistics();
	public CardLoadingWithEbankingInfo getCardLoadingWithEbankingInfo();
	public String getCamiproCookie();
	public String getLastUpdateDate();
	
}
