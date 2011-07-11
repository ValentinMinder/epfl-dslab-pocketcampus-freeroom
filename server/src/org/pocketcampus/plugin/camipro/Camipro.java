//package org.pocketcampus.plugin.camipro;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.pocketcampus.core.plugin.IPlugin;
//import org.pocketcampus.core.plugin.PublicMethod;
//import org.pocketcampus.plugin.authentication.AuthenticationSessions;
//import org.pocketcampus.plugin.camipro.elements.BalanceServer;
//import org.pocketcampus.plugin.camipro.elements.EbankingServer;
//import org.pocketcampus.plugin.camipro.elements.TransactionServer;
//import org.pocketcampus.plugin.camipro.elements.TransactionsServer;
//import org.pocketcampus.shared.plugin.authentication.AuthToken;
//import org.pocketcampus.shared.plugin.camipro.BalanceBean;
//import org.pocketcampus.shared.plugin.camipro.EbankingBean;
//import org.pocketcampus.shared.plugin.camipro.TransactionBean;
//import org.pocketcampus.shared.utils.URLLoader;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//
///**
// * IPlugin server class for the Camipro plugin.
// * 
// * Get the required data from the Camipro server.
// * The client side has to provide the {@link AuthToken} when requesting any information.
// * 
// * @status Complete
// * 
// * @author Jonas, Johan
// *
// */
//public class Camipro implements IPlugin {
//
//	private static final String BASE_URL = "https://cmp2www.epfl.ch/ws/";
//	private static final String BALANCE_URL = BASE_URL + "balance";
//	private static final String TRANSACTIONS_URL = BASE_URL + "transactions";
//	private static final String EBANKING_URL = BASE_URL + "ebanking";
//
//	private static final Gson gson_ = new Gson();
//	
//	/**
//	 * Get the current balance 
//	 * @param request
//	 * @return
//	 */
//	@PublicMethod
//	public BalanceBean getBalance(HttpServletRequest request) {
//
//		AuthToken token = getToken(request);
//		String username = token.getUsername();
//		String password = AuthenticationSessions.getPassword(token);
//		
//		String result = null;
//		try {
//			result = URLLoader.getSource(BALANCE_URL, username, password);
//		} catch (IOException e) {
//			return null;
//		}
//		
//		if(result == null) {
//			return null;
//		}
//		
//		try {
//			BalanceServer bs = gson_.fromJson(result, BalanceServer.class);
//			return new BalanceBean(bs.getPersonalAccountBalance());
//		} catch (JsonSyntaxException e) {
//			return null;
//		}
//	}
//	
//	/**
//	 * Get the last transactions.
//	 * It is not possible to get old transactions, we can only get the last 10.
//	 * @param request
//	 * @return
//	 */
//	@PublicMethod
//	public List<TransactionBean> getTransactions(HttpServletRequest request) {
//
//		AuthToken token = getToken(request);
//		String username = token.getUsername();
//		String password = AuthenticationSessions.getPassword(token);
//		
//		String result = null;
//		try {
//			result = URLLoader.getSource(TRANSACTIONS_URL, username, password);
//		} catch (IOException e) {
//			return null;
//		}
//		
//		if(result == null) {
//			return null;
//		}
//		
//		TransactionsServer tss;
//		try {
//			tss = gson_.fromJson(result, TransactionsServer.class);
//		} catch (JsonSyntaxException e1) {
//			return null;
//		}
//		
//		// To parse the date
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		
//		List<TransactionBean> l = new ArrayList<TransactionBean>();
//		for(TransactionServer ts : tss.getLastTransactionsList().getLastTransactions()) {
//			Date d;
//			try {
//				d = formatter.parse(ts.getTransactionDate());
//			} catch (ParseException e) {
//				d = new Date();
//			}
//			
//			l.add(new TransactionBean(ts.getTransactionType(), ts.getElementPrettyDescription(), d, ts.getTransactionAmount()));
//		}
//		
//		Collections.sort(l, c);
//		
//		return l;
//	}
//
//	/**
//	 * Get all the ebanking info we could use.
//	 * 
//	 * @param request
//	 * @return
//	 */
//	@PublicMethod
//	public EbankingBean getEbanking(HttpServletRequest request) {
//		
//		AuthToken token = getToken(request);
//		String username = token.getUsername();
//		String password = AuthenticationSessions.getPassword(token);
//		
//		String result = null;
//		try {
//			result = URLLoader.getSource(EBANKING_URL, username, password);
//		} catch (IOException e) {
//			return null;
//		}
//		
//		if(result == null) {
//			return null;
//		}
//		
//		try {
//			EbankingServer ebs = gson_.fromJson(result, EbankingServer.class);
//			return new EbankingBean(ebs.getPaidNameTo(), ebs.getAccountNr(), ebs.getBvrReference(), ebs.getBvrReadableReference(), ebs.getTotalAmount1M(), ebs.getTotalAmount3M(), ebs.getAverageAmount3M());
//		} catch (JsonSyntaxException e) {
//			return null;
//		}
//	}
//	
//	/**
//	 * Get the token from the request
//	 * @param request
//	 * @return null if not token available
//	 */
//	private AuthToken getToken(HttpServletRequest request) {
//		String json = null;
//		try {
//			json = request.getParameter("token");
//			
//			return new Gson().fromJson(json, AuthToken.class);
//		} catch (Exception e) {
//			// The token stays empty
//			return null;
//		}
//	}
//	
//	// Comparator used to sort the transactions
//	private static Comparator<TransactionBean> c = new Comparator<TransactionBean>() {
//		@Override
//		public int compare(TransactionBean o1, TransactionBean o2) {
//			return o2.getDate().compareTo(o1.getDate());
//		}
//	};
//}
