package org.pocketcampus.plugin.satellite.server.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pocketcampus.plugin.satellite.shared.Affluence;

public class AffluenceParser {

	private final static String AFFLUENCE_URL = "http://sat.epfl.ch/affluence";
	private String affluence;

	public AffluenceParser() {
		Document doc = null;
		affluence = "";

		try {
			doc = Jsoup.connect(AFFLUENCE_URL).get();
		} catch (IOException e) {
			System.out.println("Error getting the Document!");
			e.printStackTrace();
		}

		if (doc != null) {
			affluence = doc.text();
		} 

	}

	public Affluence getAffluence() {
		Affluence a = Affluence.ERROR;
		
		switch (Integer.valueOf(affluence)) {
		case 0 :
			a = Affluence.EMPTY;
			break;
		case 1 :
			a = Affluence.MEDIUM;
			break;
		case 2 :
			a = Affluence.CROWDED;
			break;
		case 3 :
			a = Affluence.FULL;
			break;
		case 4 :
			a = Affluence.CLOSED;
			break;
		default :
			a = Affluence.ERROR;
			break;
		}
		
		return a;
	}
	
}
