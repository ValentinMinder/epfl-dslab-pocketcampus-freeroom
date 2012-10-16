package org.pocketcampus.plugin.camipro.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.shared.TequilaToken;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * CamiproModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Camipro plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.camiproCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CamiproModel extends PluginModel implements ICamiproModel {
	
	/**
	 * Some constants.
	 */
	private static final String CAMIPRO_STORAGE_NAME = "CAMIPRO_STORAGE_NAME";
	private static final String CAMIPRO_COOKIE_KEY = "CAMIPRO_COOKIE_KEY";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	ICamiproView mListeners = (ICamiproView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<Transaction> iTransactions;
	private Double iBalance;
	private CardStatistics iCardStatistics;
	private CardLoadingWithEbankingInfo iCardLoadingWithEbankingInfo;
	private String lastUpdate;
	private TequilaToken tequilaToken;
	private boolean forceReauth;
	
	/**
	 * Data that need to be persistent.
	 */
	private String camiproCookie;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public CamiproModel(Context context) {
		iStorage = context.getSharedPreferences(CAMIPRO_STORAGE_NAME, 0);
		camiproCookie = iStorage.getString(CAMIPRO_COOKIE_KEY, null);
		
	}
	
	public boolean getForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean val) {
		forceReauth = val;
	}
	
	/**
	 * Setter and getter for iTransactions
	 */
	public List<Transaction> getTransactions() {
		return iTransactions;
	}
	public void setTransactions(List<Transaction> trans) {
		iTransactions = trans;
		mListeners.transactionsUpdated();
	}
	
	/**
	 * Setter and getter for iBalance
	 */
	public Double getBalance() {
		return iBalance;
	}
	public void setBalance(Double bal) {
		iBalance = bal;
		mListeners.balanceUpdated();
	}
	
	/**
	 * Setter and getter for iCardStatistics
	 */
	public CardStatistics getCardStatistics() {
		return iCardStatistics;
	}
	public void setCardStatistics(CardStatistics val) {
		iCardStatistics = val;
		mListeners.cardStatisticsUpdated();
	}
	
	/**
	 * Setter and getter for iCardLoadingWithEbankingInfo
	 */
	public CardLoadingWithEbankingInfo getCardLoadingWithEbankingInfo() {
		return iCardLoadingWithEbankingInfo;
	}
	public void setCardLoadingWithEbankingInfo(CardLoadingWithEbankingInfo val) {
		iCardLoadingWithEbankingInfo = val;
		mListeners.cardLoadingWithEbankingInfoUpdated();
	}
	
	/**
	 * Setter and getter for lastUpdate
	 */
	public String getLastUpdateDate() {
		return lastUpdate;
	}
	public void setLastUpdateDate(String aDate) {
		lastUpdate = aDate;
		mListeners.lastUpdateDateUpdated();
	}
	
	/**
	 * Setter and getter for camiproCookie
	 */
	public String getCamiproCookie() {
		return camiproCookie;
	}
	public void setCamiproCookie(String aCamiproCookie) {
		camiproCookie = aCamiproCookie;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(CAMIPRO_COOKIE_KEY, camiproCookie);
			editor.commit();
		}
	}
	
	/**
	 * Setter and getter for tequilaToken
	 */
	public TequilaToken getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(TequilaToken arg) {
		tequilaToken = arg;
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICamiproView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public ICamiproView getListenersToNotify() {
		return mListeners;
	}
	
}
