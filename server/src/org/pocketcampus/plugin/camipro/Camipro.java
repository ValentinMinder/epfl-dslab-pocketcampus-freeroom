package org.pocketcampus.plugin.camipro;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.camipro.BalanceBean;
import org.pocketcampus.shared.plugin.camipro.TransactionBean;
import org.pocketcampus.shared.utils.URLLoader;

import com.google.gson.Gson;

public class Camipro implements IPlugin {

	private static String urlBalance_ = "https://cmp2www.epfl.ch/servicesdev/ws/balance";
	private static String urlTransactions_ = "https://cmp2www.epfl.ch/servicesdev/ws/transactions";

	private Gson gson_ = new Gson();
	
	@PublicMethod
	public BalanceBean getBalance(HttpServletRequest request) {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		String result = null;
		try {
			result = URLLoader.getSource(urlBalance_, username, password);
		} catch (IOException e) {
			return null;
		}
		
		if(result == null) {
			return null;
		}
		
		BalanceServer bs = gson_.fromJson(result, BalanceServer.class);
		
		return new BalanceBean(bs.getPersonalAccountBalance());
	}
	
	@PublicMethod
	public List<TransactionBean> getTransactions(HttpServletRequest request) {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		String result = null;
		try {
			result = URLLoader.getSource(urlTransactions_, username, password);
		} catch (IOException e) {
			return null;
		}
		
		if(result == null) {
			return null;
		}
		
		TransactionsServer tss = gson_.fromJson(result, TransactionsServer.class);

		List<TransactionBean> l = new ArrayList<TransactionBean>();
		for(TransactionServer ts : tss.getLastTransactionsList().getLastTransactions()) {
			Date d;
			DateFormat df = DateFormat.getDateTimeInstance();
			try {
				d = df.parse(ts.getTransactionDate());
			} catch (ParseException e) {
				d = new Date();
			}
			
			l.add(new TransactionBean(ts.getTransactionType(), ts.getElementPrettyDescription(), d, ts.getTransactionAmount()));
		}
		
		return l;
	}
}
