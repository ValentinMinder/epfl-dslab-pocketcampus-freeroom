package org.pocketcampus.plugin.camipro.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class CamiproModel extends PluginModel implements ICamiproModel {
	
	private CamiproModel() {
		
	}
	
	public static CamiproModel getInstance(){
		if(self == null)
			self = new CamiproModel();
		return self;
	}
	
	public static CamiproModel killInstance(){
		self = new CamiproModel();
		return self;
	}
	
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

	@Override
	public String getLastUpdateDate() {
		return lastUpdate;
	}
	
	public void setTransactions(List<Transaction> trans) {
		iTransactions = trans;
		lastUpdate = getCurrentDate();
		mListeners.transactionsUpdated();
	}

	public void setBalance(Double bal) {
		iBalance = bal;
		lastUpdate = getCurrentDate();
		mListeners.balanceUpdated();
	}

	public void setCardStatistics(CardStatistics val) {
		iCardStatistics = val;
		lastUpdate = getCurrentDate();
		mListeners.cardStatisticsUpdated();
	}
	
	public void setCardLoadingWithEbankingInfo(CardLoadingWithEbankingInfo val) {
		iCardLoadingWithEbankingInfo = val;
		lastUpdate = getCurrentDate();
		mListeners.cardLoadingWithEbankingInfoUpdated();
	}
	
	private String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH'h'mm");
		return sdf.format(new Date());
	}
	
	ICamiproView mListeners = (ICamiproView) getListeners();
	
	public void setCamiproCookie(String aCamiproCookie) {
		camiproCookie = aCamiproCookie;
	}
	
	//TODO have camipro cookie saved in storage
	private String camiproCookie = null;
	
	private List<Transaction> iTransactions = null;
	private Double iBalance = null;
	private CardStatistics iCardStatistics = null;
	private CardLoadingWithEbankingInfo iCardLoadingWithEbankingInfo = null;
	
	private String lastUpdate = null;
	
	private static CamiproModel self = null;

}
