package org.pocketcampus.plugin.camipro;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.authentication.AuthenticationSessions;
import org.pocketcampus.plugin.camipro.elements.BalanceServer;
import org.pocketcampus.plugin.camipro.elements.TransactionServer;
import org.pocketcampus.plugin.camipro.elements.TransactionsServer;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
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

		AuthToken token = getToken(request);
		String username = token.getUsername();
		String password = AuthenticationSessions.getPassword(token);
		
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

		AuthToken token = getToken(request);
		String username = token.getUsername();
		String password = AuthenticationSessions.getPassword(token);
		
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		List<TransactionBean> l = new ArrayList<TransactionBean>();
		for(TransactionServer ts : tss.getLastTransactionsList().getLastTransactions()) {
			Date d;
			try {
				d = formatter.parse(ts.getTransactionDate());
			} catch (ParseException e) {
				d = new Date();
			}
			
			l.add(new TransactionBean(ts.getTransactionType(), ts.getElementPrettyDescription(), d, ts.getTransactionAmount()));
		}
		
		Collections.sort(l, c);
		
		return l;
	}
	
	/**
	 * Get the token from the request
	 * @param request
	 * @return null if not token available
	 */
	private AuthToken getToken(HttpServletRequest request) {
		String json = null;
		try {
			json = request.getParameter("token");
			
			return new Gson().fromJson(json, AuthToken.class);
		} catch (Exception e) {
			// The token stays empty
			return null;
		}
	}
	
	// Comparator used to sort the transactions
	private static Comparator<TransactionBean> c = new Comparator<TransactionBean>() {
		@Override
		public int compare(TransactionBean o1, TransactionBean o2) {
			return o2.getDate().compareTo(o1.getDate());
		}
	};
}
