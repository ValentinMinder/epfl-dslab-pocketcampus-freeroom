package org.pocketcampus.plugin.camipro.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.plugin.camipro.shared.TequilaToken;
import org.pocketcampus.platform.shared.utils.Cookie;
import org.pocketcampus.plugin.camipro.shared.BalanceAndTransactions;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.camipro.shared.CamiproSession;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.SendMailResult;
import org.pocketcampus.plugin.camipro.shared.StatsAndLoadingInfo;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/**
 * CamiproServiceImpl
 * 
 * The implementation of the server side of the Camipro Plugin.
 * 
 * It fetches the user's Camipro data from the Camipro servers. And sends the
 * e-banking information to the user by email.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproServiceImpl implements CamiproService.Iface {

	private boolean useAPI;

	public CamiproServiceImpl() {
		System.out.println("Starting Camipro plugin server ...");
		useAPI = true;
	}
	
	@Override
	public TequilaToken getTequilaTokenForCamipro() throws TException {
		System.out.println("getTequilaToken");
		try {
			String cmdLine = "curl --include https://cmp2www.epfl.ch/ws/balance";
			String resp = executeCommand(cmdLine, "UTF-8");
			Cookie cookie = new Cookie();
			TequilaToken teqToken = new TequilaToken();
			for (String header : resp.split("\r\n")) {
				String shdr[] = header.split(":", 2);
				if (shdr.length != 2)
					continue;
				if ("Set-Cookie".equalsIgnoreCase(shdr[0])) {
					cookie.addFromHeader(shdr[1].trim());
				} else if ("Location".equalsIgnoreCase(shdr[0])) {
					URL url = new URL(shdr[1].trim());
					MultiMap<String> params = new MultiMap<String>();
					UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
					teqToken.setITequilaKey(params.getString("requestkey"));
				}
			}
			teqToken.setLoginCookie(cookie.cookie());
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException(
					"Failed to getTequilaToken from upstream server");
		}
	}

	@Override
	public CamiproSession getCamiproSession(TequilaToken iTequilaToken)
			throws TException {
		System.out.println("getCamiproSession");
		// PocketCampusServer.pushNotifMap(iTequilaToken, "camipro",
		// "chamsedd");
		return new CamiproSession(iTequilaToken == null ? ""
				: iTequilaToken.getLoginCookie());
	}

	@Override
	public BalanceAndTransactions getBalanceAndTransactions(
			CamiproRequest iRequest) throws TException {
		System.out.println("getBalanceAndTransactions");
		// PocketCampusServer.pushNotifSend("camipro", Arrays.asList(new
		// String[]{"chamsedd"}), new HashMap<String, String>());
		if (useAPI)
			return getBalanceAndTransactionsWAPI(iRequest);

		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/sertrans",
					cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new BalanceAndTransactions(404);
			// throw new
			// TException("getBalanceAndTransactions: Failed to get data from Camipro upstream server");
		}
		if (page == null) {
			System.out.println("not logged in");
			return new BalanceAndTransactions(407);
		}

		String date = getSubstringBetween(page, "<sup>1</sup>(", ")");

		double tBalance = 0.0;
		LinkedList<Transaction> tTransactions = new LinkedList<Transaction>();

		page = getSubstringBetween(
				page,
				"<table class='table' width='600px' style='margin-bottom: 10px;'>",
				"</table>");
		for (String i : page.split("</tr>")) {
			if (i.indexOf("<td></td><td></td>") != -1) {
				tBalance = Double
						.parseDouble(getSubstringBetween(
								i,
								"<td style='text-align:right;vertical-align:bottom;'><b>",
								"</b></td>"));
			} else if (i.startsWith("<td style='text-align:left'>")) {
				String[] trans = i.split("</td>");
				if (trans.length < 4)
					continue;
				tTransactions.add(new Transaction(getSubstringBetween(trans[0],
						">", "<"), getSubstringBetween(trans[1], ">", "<"),
						getSubstringBetween(trans[2], ">", "<"), Double
								.parseDouble(getSubstringBetween(trans[3], ">",
										"<"))));
			}
		}

		BalanceAndTransactions bt = new BalanceAndTransactions(200);
		bt.setIBalance(tBalance);
		bt.setITransactions(tTransactions);
		bt.setIDate(date);
		return bt;
	}

	@Override
	public StatsAndLoadingInfo getStatsAndLoadingInfo(CamiproRequest iRequest)
			throws TException {
		System.out.println("getStatsAndLoadingInfo");
		if (useAPI)
			return getStatsAndLoadingInfoWAPI(iRequest);

		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking",
					cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new StatsAndLoadingInfo(404);
			// throw new
			// TException("getStatsAndLoadingInfo: Failed to get data from Camipro upstream server");
		}
		if (page == null) {
			System.out.println("not logged in");
			return new StatsAndLoadingInfo(407);
		}

		String PaymentFor = getSubstringBetween(page, "name=\"PaymentFor\">",
				"<");
		String ReferenceNbr = getLastSubstringBetween(page, "\"",
				"\" name=\"ReferenceNr\"");
		String AccountNbr = getLastSubstringBetween(page, "\"",
				"\" name=\"Account\"");

		String Total1M = getSubstringBetween(page, "<h5>", "</h5>");
		String Total3M = getLastSubstringBetween(page, "<h5>", "</h5>");
		double dTotal1M = Double.parseDouble(getSubstringBetween(Total1M,
				"CHF ", "<"));
		double dTotal3M = Double.parseDouble(getSubstringBetween(Total3M,
				"CHF ", " "));

		CardStatistics tCardStatistics = new CardStatistics(dTotal1M, dTotal3M);
		CardLoadingWithEbankingInfo tCardLoadingWithEbankingInfo = new CardLoadingWithEbankingInfo(
				PaymentFor, AccountNbr, ReferenceNbr);

		StatsAndLoadingInfo sl = new StatsAndLoadingInfo(200);
		sl.setICardStatistics(tCardStatistics);
		sl.setICardLoadingWithEbankingInfo(tCardLoadingWithEbankingInfo);
		return sl;
	}

	@Override
	public SendMailResult sendLoadingInfoByEmail(CamiproRequest iRequest)
			throws TException {
		System.out.println("sendLoadingInfoByEmail");
		if (useAPI)
			return sendLoadingInfoByEmailWAPI(iRequest);

		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			System.out.println("request language " + iRequest.getILanguage());
			// switch language first
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking-"
					+ iRequest.getILanguage(), cookie);
			// then get data
			page = getPageWithCookie("https://cmp2www.epfl.ch/client/ebanking",
					cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if (page == null) {
			System.out.println("not logged in");
			return new SendMailResult(407);
		}

		// fetch email address
		String emailAddress = getSubstringBetween(page, "name='email'", ">");
		emailAddress = getSubstringBetween(emailAddress, "'", "'");

		// now send email
		try {
			page = getPageWithCookie(
					"https://cmp2www.epfl.ch/client/services/ebanking_email.php?email="
							+ emailAddress, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if (page == null) {
			System.out.println("not logged in");
			return new SendMailResult(407);
		}

		SendMailResult mr = new SendMailResult(200);
		mr.setIResultText(page);
		return mr;
	}

	public BalanceAndTransactions getBalanceAndTransactionsWAPI(
			CamiproRequest iRequest) throws TException {
		// System.out.println(iRequest.toString());
		String balPage = null;
		String trxPage = null;
		BalanceJson tBalance = null;
		TransactionsJson tTransactions = null;
		Gson gson = getGson();
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			balPage = getPageWithCookie("https://cmp2www.epfl.ch/ws/balance",cookie);
			trxPage = getPageWithCookie("https://cmp2www.epfl.ch/ws/transactions", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new BalanceAndTransactions(404);
		}
		if (balPage == null || trxPage == null) {
			System.out.println("not logged in");
			return new BalanceAndTransactions(407);
		}

		try {
			tBalance = gson.fromJson(balPage, BalanceJson.class);
			tTransactions = gson.fromJson(trxPage, TransactionsJson.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return new BalanceAndTransactions(404);
		}
		if (!"Success".equals(tBalance.Status)
				|| !"Success".equals(tTransactions.Status)) {
			System.out.println("camipro upstream server has failed");
			return new BalanceAndTransactions(404);
		}

		LinkedList<Transaction> decodedTrx = new LinkedList<Transaction>();
		if (tTransactions.LastTransactionsList.LastTransactions != null)
			for (TransactionsJson.TransactionsListJson.TransactionJson t : tTransactions.LastTransactionsList.LastTransactions) {
				if (t.TransactionDate == null || t.TransactionType == null
						|| t.ElementDescription == null) {
					continue;
				}
				decodedTrx.add(new Transaction(
						transcribeDate(t.TransactionDate), t.TransactionType,
						t.ElementDescription, t.TransactionAmount));
			}

		BalanceAndTransactions bt = new BalanceAndTransactions(200);
		bt.setIBalance(tBalance.PersonalAccountBalance);
		bt.setITransactions(decodedTrx);
		bt.setIDate(transcribeDate(tBalance.LastUpdated));
		return bt;
	}

	public StatsAndLoadingInfo getStatsAndLoadingInfoWAPI(
			CamiproRequest iRequest) throws TException {
		String ebnkPage = null;
		EbankingJson tEbanking = null;
		Gson gson = getGson();
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			ebnkPage = getPageWithCookie("https://cmp2www.epfl.ch/ws/ebanking",
					cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new StatsAndLoadingInfo(404);
		}
		if (ebnkPage == null) {
			System.out.println("not logged in");
			return new StatsAndLoadingInfo(407);
		}

		try {
			tEbanking = gson.fromJson(ebnkPage, EbankingJson.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return new StatsAndLoadingInfo(404);
		}
		if (!"Success".equals(tEbanking.Status)) {
			System.out.println("camipro upstream server has failed");
			return new StatsAndLoadingInfo(404);
		}

		CardStatistics tCardStatistics = new CardStatistics(
				tEbanking.TotalAmount1M, tEbanking.TotalAmount3M);
		CardLoadingWithEbankingInfo tCardLoadingWithEbankingInfo = new CardLoadingWithEbankingInfo(
				tEbanking.PaidNameTo, tEbanking.AccountNr,
				tEbanking.BvrReadableReference);

		StatsAndLoadingInfo sl = new StatsAndLoadingInfo(200);
		sl.setICardStatistics(tCardStatistics);
		sl.setICardLoadingWithEbankingInfo(tCardLoadingWithEbankingInfo);
		return sl;
	}

	public SendMailResult sendLoadingInfoByEmailWAPI(CamiproRequest iRequest)
			throws TException {
		String ebmPage = null;
		EbankingemailJson tEbankingemail = null;
		Gson gson = getGson();
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getCamiproCookie());

		try {
			ebmPage = getPageWithCookie(
					"https://cmp2www.epfl.ch/ws/ebankingemail?lang="
							+ iRequest.getILanguage(), cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if (ebmPage == null) {
			System.out.println("not logged in");
			return new SendMailResult(407);
		}

		try {
			tEbankingemail = gson.fromJson(ebmPage, EbankingemailJson.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return new SendMailResult(404);
		}
		if (!"Success".equals(tEbankingemail.Status)) {
			System.out.println("camipro upstream server has failed");
			return new SendMailResult(404);
		}

		SendMailResult mr = new SendMailResult(200);
		mr.setIResultText(tEbankingemail.Message);
		return mr;
	}

	/**
	 * HELPER FUNCTIONS
	 */

	private Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(double.class, new EnglishDoubleDeserializer());
		return builder.create();
	}

	private String executeCommand(String cmd, String encoding)
			throws IOException {
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(cmd);
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("executeCommand: waitFor Interrupted");
		}

		// HACK to avoid a dependency on Commons IO
		Scanner scanner = null;
		try {
			scanner = new Scanner(pr.getInputStream(), encoding)
					.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private String getPageWithCookie(String url, Cookie cookie)
			throws IOException {
		String resp = executeCommand("curl --cookie " + cookie.cookie() + " "
				+ url, "UTF-8");
		if (resp.length() == 0)
			return null;
		return resp;
	}

	private String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if (b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if (a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}

	private String getLastSubstringBetween(String orig, String before,
			String after) {
		int a = orig.lastIndexOf(after);
		if (a != -1) {
			orig = orig.substring(0, a);
		}
		int b = orig.lastIndexOf(before);
		if (b != -1) {
			orig = orig.substring(b + before.length());
		}
		return orig;
	}

	private String transcribeDate(String date) {
		SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat out = new SimpleDateFormat("dd.MM.yy HH'h'mm");
		try {
			date = out.format(in.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * HELPER CLASSES
	 */

	public class BalanceJson {
		public double PersonalAccountBalance;
		public String LastUpdated;
		public String Status;
	}

	public class EbankingJson {
		public String PaidNameTo;
		public String AccountNr;
		public String BvrReadableReference;
		public double TotalAmount1M;
		public double TotalAmount3M;
		public String Status;
	}

	public class TransactionsJson {
		public TransactionsListJson LastTransactionsList;
		public String Status;

		public class TransactionsListJson {
			public List<TransactionJson> LastTransactions;

			public class TransactionJson {
				public String TransactionType;
				public String ElementDescription;
				public String TransactionDate;
				public double TransactionAmount;
			}
		}
	}

	public class EbankingemailJson {
		public String Message;
		public String Status;
	}

	/** Special deserializer that uses a set format to parse double, rather than depending on the current locale. */
	private static final class EnglishDoubleDeserializer implements
			JsonDeserializer<Double> {

		private static final NumberFormat NUMBER_FORMAT = NumberFormat
				.getInstance(Locale.ENGLISH);

		@Override
		public Double deserialize(JsonElement elem, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonPrimitive primitive = (JsonPrimitive) elem;
			if (primitive.isNumber()) {
				return primitive.getAsDouble();
			}
			try {
				return NUMBER_FORMAT.parse(elem.getAsString()).doubleValue();
			} catch (ParseException e) {
				throw new JsonParseException("Unexpected number format.", e);
			}
		}

	}
}
