package org.pocketcampus.plugin.camipro.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.utils.URLLoader;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;
import org.pocketcampus.plugin.camipro.server.gsonobjects.BalanceObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.EbankingObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.TransactionsObj;
import org.pocketcampus.plugin.camipro.server.gsonobjects.TransactionsObj.TransactionsList.TransactionObj;
import org.pocketcampus.plugin.camipro.shared.BalanceAndTransactions;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.StatsAndLoadingInfo;
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
/*
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

	// Comparator used to sort the transactions
	private static Comparator<Transaction> c = new Comparator<Transaction>() {
		public int compare(Transaction o1, Transaction o2) {
			return o2.xDate.compareTo(o1.xDate);
		}
	};*/

	
	
	
	@Override
	public BalanceAndTransactions getBalanceAndTransactions(SessionId aSessionId) throws TException {
		System.out.println("getBalanceAndTransactions");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(aSessionId.getCamiproCookie());
		
		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/sertrans", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("getBalanceAndTransactions: Failed to get data from Camipro upstream server");
		}
        
		double tBalance = 0.0;
		LinkedList<Transaction> tTransactions = new LinkedList<Transaction>();
		
		page = getSubstringBetween(page, "<table class='table' width='600px' style='margin-bottom: 10px;'>", "</table>");
		for (String i : page.split("</tr>")) {
			if(i.indexOf("<td></td><td></td>") != -1) {
				tBalance = Double.parseDouble(getSubstringBetween(i, "<td style='text-align:right;vertical-align:bottom;'><b>", "</b></td>"));
			} else if(i.startsWith("<td style='text-align:left'>")) {
				String[] trans = i.split("</td>");
				if(trans.length < 4)
					continue;
				tTransactions.add(new Transaction(
						getSubstringBetween(trans[0], ">", "<"),
						getSubstringBetween(trans[1], ">", "<"),
						getSubstringBetween(trans[2], ">", "<"),
						Double.parseDouble(getSubstringBetween(trans[3], ">", "<"))));
			}
		}
		
		return new BalanceAndTransactions(tBalance, tTransactions);
	}

	@Override
	public StatsAndLoadingInfo getStatsAndLoadingInfo(SessionId aSessionId) throws TException {
		System.out.println("getStatsAndLoadingInfo");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(aSessionId.getCamiproCookie());
		
		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("getStatsAndLoadingInfo: Failed to get data from Camipro upstream server");
		}
		
		String PaymentFor = getSubstringBetween(page, "name=\"PaymentFor\">", "<");
		String ReferenceNbr = getLastSubstringBetween(page, "\"", "\" name=\"ReferenceNr\"");
		String AccountNbr = getLastSubstringBetween(page, "\"", "\" name=\"Account\"");
		PaymentFor = StringEscapeUtils.unescapeHtml4(PaymentFor);
		
		String Total1M = getSubstringBetween(page, "<h5>", "</h5>");
		String Total3M = getLastSubstringBetween(page, "<h5>", "</h5>");
		double dTotal1M = Double.parseDouble(getSubstringBetween(Total1M, "CHF ", "<"));
		double dTotal3M = Double.parseDouble(getSubstringBetween(Total3M, "CHF ", " "));
		
		CardStatistics tCardStatistics = new CardStatistics(dTotal1M, dTotal3M);
		CardLoadingWithEbankingInfo tCardLoadingWithEbankingInfo = new CardLoadingWithEbankingInfo(PaymentFor, AccountNbr, ReferenceNbr);
		return new StatsAndLoadingInfo(tCardStatistics, tCardLoadingWithEbankingInfo);
	}
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("Cookie", cookie.cookie());
		Scanner reader = new Scanner(conn.getInputStream());
		StringBuilder token = new StringBuilder ();
		while(reader.hasNextLine())
			token.append(reader.nextLine());
		return token.toString();
	}
	
	private String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}
	
	private String getLastSubstringBetween(String orig, String before, String after) {
		int a = orig.lastIndexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		int b = orig.lastIndexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		return orig;
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
	
}
