package org.pocketcampus.plugin.camipro.android.iface;

import java.util.List;

import org.pocketcampus.plugin.camipro.shared.EbankingBean;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public interface ICamiproModel {
	public List<Transaction> getTransactions();
	public Double getBalance();
	public EbankingBean getEbanking();
	public String getCamiproCookie();
	
}
