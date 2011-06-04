package org.pocketcampus.plugin.library;
import java.util.ArrayList;

import org.pocketcampus.shared.plugin.library.BookBean;


public class Test {
	public static void main(String[] args) {
		NebisSession session = new NebisSession();
		RequestHandler requestHandler = new RequestHandler(session);
		Search search = new Search(requestHandler);
		ArrayList<BookBean> results = search.getResults("einstein", 3);
		System.out.println(results);
		
		//BookInfo bookInfo = new BookInfo(requestHandler);
		//BookAvailabilityBean availability = bookInfo.getAvailability(results.get(5).getDocNumber());
	}
}
