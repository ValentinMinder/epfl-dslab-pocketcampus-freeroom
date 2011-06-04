package org.pocketcampus.plugin.library;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.shared.plugin.library.BookBean;
import org.pocketcampus.shared.utils.StringUtils;


public class Search {
	private String lastSearchTerms_;
	private RequestHandler requestHandler_;

	public Search(RequestHandler requestHandler) {
		requestHandler_ = requestHandler;
	}

	private ArrayList<BookBean> getResults(String searchTerms) {
		lastSearchTerms_ = searchTerms;
		String params = getSearchParameters(searchTerms);
		Document resultPage = requestHandler_.loadNebisPage(params);
		return parseResultPage(resultPage);
	}

	public ArrayList<BookBean> getResults(String searchTerms, int page) {
		if(page == 1) {
			return getResults(searchTerms);
		}

		if(searchTerms != lastSearchTerms_) {
			getResults(searchTerms);
		}

		String params = getPageParameters(page);
		Document resultPage = requestHandler_.loadNebisPage(params);
		ArrayList<BookBean> results = parseResultPage(resultPage);

		return results;
	}

	@SuppressWarnings("unused")
	private ArrayList<BookBean> parseResultPage(Document resultPage) {
		ArrayList<BookBean> results = new ArrayList<BookBean>();

		if(checkForErrors(resultPage)) {
			return results;
		}

		Elements resultTrs = resultPage.getElementsByAttributeValue("valign", "baseline");

		String author;
		String tempLink;
		int year;
		String title;
		ArrayList<String> libraries;
		String permalink;
		int docNumber = 0;

		for(Element resultTr : resultTrs) {
			Elements resultTds = resultTr.getElementsByTag("td");

			// temp link (useless?)
			tempLink = resultTds.get(0).getElementsByTag("a").attr("href").toString();

			// author
			author = resultTds.get(3).text();

			// title
			String titleScript = resultTds.get(4).toString();
			title = StringUtils.stringBetween(titleScript, "</script> ", " </td>");

			// date
			String dateScript = resultTds.get(5).getElementsByTag("script").toString();
			try {
				year = Integer.parseInt(StringUtils.stringBetween(dateScript, "v1=\"", "----"));
			} catch (NumberFormatException e) {
				year = 0;
			}

			// libraries
			libraries = new ArrayList<String>();
			for(Element libraryEl : resultTds.get(6).getElementsByTag("a")) {
				libraries.add(libraryEl.text());
			}

			// permalink
			permalink = resultTds.get(6).getElementsByTag("a").attr("href");

			// doc number
			try {
				docNumber = Integer.parseInt(StringUtils.stringBetween(permalink, "&doc_number=", "&year="));
			} catch (NumberFormatException e) {
				docNumber = 0;
			}

			// unescape the result
			title = StringUtils.unescapeHTML(title);
			author = StringUtils.unescapeHTML(author);

			results.add(new BookBean(title, author, year, docNumber, libraries));
		}

		return results;
	}

	private boolean checkForErrors(Document resultPage) {
		Elements feedbackbars = resultPage.getElementsByClass("feedbackbar");

		if(feedbackbars.size() == 0) {
			return true;
		}

		String text = feedbackbars.get(0).text();
		boolean error = !text.equals("");

		return error;
	}

	private String getPageParameters(int pageNumber) {
		int startIndex = (pageNumber-1)*10 + 1;
		startIndex = Math.max(startIndex, 1);

		return "?func=short-jump&jump=" + startIndex;
	}

	private String getSearchParameters(String searchTerms) {
		return "?func=find-c&x=0&y=0&filter_code_1=WLN&filter_request_1=&filter_code_5=WSL&filter_request_5=E02+or+E14+or+E49+or+E83+or+E84+or+E92&filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFT&filter_request_4=&filter_code_6=WKO&filter_request_6=&ccl_term=" + searchTerms;
		//return "?func=find-b&find_code=WRD&x=0&y=0&adjacent=N&request=" + searchTerms;
	}

}
