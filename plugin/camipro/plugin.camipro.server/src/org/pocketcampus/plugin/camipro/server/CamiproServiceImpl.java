package org.pocketcampus.plugin.camipro.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;
import org.pocketcampus.plugin.camipro.shared.BalanceAndTransactions;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.SendMailResult;
import org.pocketcampus.plugin.camipro.shared.StatsAndLoadingInfo;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class CamiproServiceImpl implements CamiproService.Iface {
	
	
	public CamiproServiceImpl() {
		System.out.println("Starting Camipro plugin server ...");
	}
	
	@Override
	public BalanceAndTransactions getBalanceAndTransactions(CamiproRequest iRequest) throws TException {
		System.out.println("getBalanceAndTransactions");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());
		
		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/sertrans", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new BalanceAndTransactions(404);
			//throw new TException("getBalanceAndTransactions: Failed to get data from Camipro upstream server");
		}
		if(page == null) {
			System.out.println("not logged in");
			return new BalanceAndTransactions(407);
		}
        
		String date = getSubstringBetween(page, "<sup>1</sup>(", ")");
		
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
		
		BalanceAndTransactions bt = new BalanceAndTransactions(200);
		bt.setIBalance(tBalance);
		bt.setITransactions(tTransactions);
		bt.setIDate(date);
		return bt;
	}

	@Override
	public StatsAndLoadingInfo getStatsAndLoadingInfo(CamiproRequest iRequest) throws TException {
		System.out.println("getStatsAndLoadingInfo");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());
		
		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new StatsAndLoadingInfo(404);
			//throw new TException("getStatsAndLoadingInfo: Failed to get data from Camipro upstream server");
		}
		if(page == null) {
			System.out.println("not logged in");
			return new StatsAndLoadingInfo(407);
		}
		
		String PaymentFor = getSubstringBetween(page, "name=\"PaymentFor\">", "<");
		String ReferenceNbr = getLastSubstringBetween(page, "\"", "\" name=\"ReferenceNr\"");
		String AccountNbr = getLastSubstringBetween(page, "\"", "\" name=\"Account\"");
		PaymentFor = StringEscapeUtils.unescapeHtml4(PaymentFor).trim();
		
		String Total1M = getSubstringBetween(page, "<h5>", "</h5>");
		String Total3M = getLastSubstringBetween(page, "<h5>", "</h5>");
		double dTotal1M = Double.parseDouble(getSubstringBetween(Total1M, "CHF ", "<"));
		double dTotal3M = Double.parseDouble(getSubstringBetween(Total3M, "CHF ", " "));
		
		CardStatistics tCardStatistics = new CardStatistics(dTotal1M, dTotal3M);
		CardLoadingWithEbankingInfo tCardLoadingWithEbankingInfo = new CardLoadingWithEbankingInfo(PaymentFor, AccountNbr, ReferenceNbr);
		
		StatsAndLoadingInfo sl = new StatsAndLoadingInfo(200);
		sl.setICardStatistics(tCardStatistics);
		sl.setICardLoadingWithEbankingInfo(tCardLoadingWithEbankingInfo);
		return sl;
	}
	
	@Override
	public SendMailResult sendLoadingInfoByEmail(CamiproRequest iRequest) throws TException {
		System.out.println("sendLoadingInfoByEmail");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());
		
		try {
			System.out.println("request language " + iRequest.getILanguage());
			// switch language first
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking-" + iRequest.getILanguage(), cookie);
			// then get data
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if(page == null) {
			System.out.println("not logged in");
			return new SendMailResult(407);
		}
		
		// fetch email address
		String emailAddress = getSubstringBetween(page, "name='email'", ">");
		emailAddress = getSubstringBetween(emailAddress, "'", "'");
		
		// now send email
		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/services/ebanking_email.php?email=" + emailAddress, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if(page == null) {
			System.out.println("not logged in");
			return new SendMailResult(407);
		}
		
		SendMailResult mr = new SendMailResult(200);
		mr.setIResultText(page);
		return mr;
	}
	
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Cookie", cookie.cookie());
		if(conn.getResponseCode() == 302)
			return null;
		return IOUtils.toString(conn.getInputStream(), "UTF-8");
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

}
