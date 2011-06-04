package org.pocketcampus.plugin.library;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.shared.utils.StringUtils;


public class BookInfo {
	private RequestHandler requestHandler_;

	public BookInfo(RequestHandler requestHandler) {
		requestHandler_ = requestHandler;
	}

	public BookAvailabilityBean getAvailability(int docNumber) {
		String params = getAvailabilityParams(docNumber);
		Document availabilityPage = requestHandler_.loadNebisPage(params);
		
		BookAvailabilityBean availability = parseAvailabilityPage(availabilityPage);
		return availability;
	}
	
	private BookAvailabilityBean parseAvailabilityPage(Document availabilityPage) {
		Elements tables = availabilityPage.getElementsByTag("table");
		
		if(tables.size() < 4) {
			return null;
		}
		
		BookAvailabilityBean bookAvailability = new BookAvailabilityBean();
		Elements lines = tables.get(5).getElementsByTag("tr");
		
		String returnDate;
		String nbRequests;
		String status;
		String library;
		String location;
		String description;
		String collection;
		String pages;
		String notes;
		
		for (Element line : lines) {
			
			Elements cells = line.getElementsByTag("td");
			
			if(cells.size() < 9) {
				continue;
			}
			
			returnDate = cells.get(1).text();
			nbRequests = cells.get(2).text();
			status = cells.get(3).text();
			library = cells.get(4).text();
			location = cells.get(5).text();
			description = cells.get(6).text();
			collection = cells.get(7).text();
			pages = cells.get(8).text();
			notes = cells.get(9).text();
			
			BookAvailabilityItemBean availabilityItem = new BookAvailabilityItemBean(library, location, collection, status, returnDate, nbRequests, description, pages, notes);
			bookAvailability.addAvailabilityItem(availabilityItem);
		}
		
		return null;
	}

	public BookDetailsBean getDetails(int docNumber) {
		return null;
	}
	
	private String getAvailabilityParams(int docNumber) {
		return "?func=item-global&doc_library=EBI01&year=&volume=&sub_library=&doc_number=" + StringUtils.pad(docNumber, 9);
		
		// details
		//return "?local_base=NEBIS&con_lng=FRE&func=find-b&find_code=SYS&request=" + docNumber;
	}
}
