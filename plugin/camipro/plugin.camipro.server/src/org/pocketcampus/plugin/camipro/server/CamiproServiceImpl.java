package org.pocketcampus.plugin.camipro.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.utils.URLLoader;
import org.pocketcampus.plugin.camipro.server.gsonobjects.BalanceObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.EbankingObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.TransactionsObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.TransactionsObj.TransactionsList.TransactionObj;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.camipro.shared.EbankingBean;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class CamiproServiceImpl implements CamiproService.Iface {
	
	// http://magicmonster.com/kb/prg/java/ssl/pkix_path_building_failed.html
	private static final String BASE_URL = "https://cmp2www.epfl.ch/ws/";
	private static final String BALANCE_URL = BASE_URL + "balance";
	private static final String TRANSACTIONS_URL = BASE_URL + "transactions";
	private static final String EBANKING_URL = BASE_URL + "ebanking";

	private static final Gson gson_ = new Gson();
	
	public CamiproServiceImpl() {
		System.out.println("Starting Camipro plugin server ...");
	}

	@Override
	public double getBalance() throws TException {
		System.out.println("getBalance called");
		try {
			String jsonReply = URLLoader.getSource(BALANCE_URL, "chamsedd", "my_strong_pass");
			BalanceObj parsedReply = gson_.fromJson(jsonReply, BalanceObj.class);
			return parsedReply.getPersonalAccountBalance();
		} catch (IOException e) {
			throw new TException("Could not connect to camipro upstream server");
		} catch (JsonSyntaxException e) {
			throw new TException("Could not parse camipro upstream server response");
		}
	}

	@Override
	public List<Transaction> getTransactions() throws TException {
		System.out.println("getTransactions called");
		try {
			String jsonReply = URLLoader.getSource(TRANSACTIONS_URL, "chamsedd", "my_strong_pass");
			TransactionsObj parsedReply = gson_.fromJson(jsonReply, TransactionsObj.class);
			List<Transaction> l = new ArrayList<Transaction>();
			for(TransactionObj ts : parsedReply.getLastTransactionsList().getLastTransactions()) {
				l.add(new Transaction(ts.getTransactionType(), ts.getElementPrettyDescription(), ts.getTransactionDate(), ts.getTransactionAmount()));
			}
			Collections.sort(l, c);
			return l;
		} catch (IOException e) {
			throw new TException("Could not connect to camipro upstream server");
		} catch (JsonSyntaxException e) {
			throw new TException("Could not parse camipro upstream server response");
		}
	}

	@Override
	public EbankingBean getEbankingBean() throws TException {
		System.out.println("getEbankingBean called");
		try {
			String jsonReply = URLLoader.getSource(EBANKING_URL, "chamsedd", "my_strong_pass");
			EbankingObj parsedReply = gson_.fromJson(jsonReply, EbankingObj.class);
			return new EbankingBean(parsedReply.getPaidNameTo(), parsedReply.getAccountNr(), parsedReply.getBvrReference(), parsedReply.getBvrReadableReference(), parsedReply.getTotalAmount1M(), parsedReply.getTotalAmount3M(), parsedReply.getAverageAmount3M());
		} catch (IOException e) {
			throw new TException("Could not connect to camipro upstream server");
		} catch (JsonSyntaxException e) {
			throw new TException("Could not parse camipro upstream server response");
		}
	}

	
	/*private AuthToken getToken(HttpServletRequest request) {
		String json = null;
		try {
			json = request.getParameter("token");
			
			return new Gson().fromJson(json, AuthToken.class);
		} catch (Exception e) {
			// The token stays empty
			return null;
		}
	}*/
	
	// Comparator used to sort the transactions
	private static Comparator<Transaction> c = new Comparator<Transaction>() {
		public int compare(Transaction o1, Transaction o2) {
			return o2.xDate.compareTo(o1.xDate);
		}
	};
}
