package org.pocketcampus.plugin.library;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.library.BookBean;

public class Library implements IPlugin {
	private NebisSession session_;
	private BookInfo bookInfo_; 
	private Search search_;
	
	public Library() {
		session_ = new NebisSession();
		RequestHandler requestHandler = new RequestHandler(session_);
		search_ = new Search(requestHandler);
		bookInfo_ = new BookInfo(requestHandler);
	}
	
	@PublicMethod
	public ArrayList<BookBean> search(HttpServletRequest request) {
		String searchTerms = request.getParameter("terms");
		String pageNumberStr = request.getParameter("pageNumber");
		
		int pageNumber = 1;
		
		if(pageNumberStr!=null && !pageNumberStr.equals("")) {
			pageNumber = Integer.parseInt(pageNumberStr);
		}
		
		ArrayList<BookBean> results = search_.getResults(searchTerms, pageNumber);
		
		return results;
	}
	
	@PublicMethod
	public BookAvailabilityBean checkAvailability(HttpServletRequest request) {
		String docNumberStr = request.getParameter("docNumber");
		int docNumber = Integer.parseInt(docNumberStr);
		
		return bookInfo_.getAvailability(docNumber);
	}
}
















