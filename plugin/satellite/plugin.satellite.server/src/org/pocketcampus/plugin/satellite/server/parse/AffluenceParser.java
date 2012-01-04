package org.pocketcampus.plugin.satellite.server.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pocketcampus.plugin.satellite.shared.Affluence;

/**
 * A class to parse the current affluence at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class AffluenceParser {
	/** The URL of the affluence page. */
	private final static String AFFLUENCE_URL = "http://sat.epfl.ch/affluence";
	/** The String we retrieve. */
	private String mAffluence;

	/**
	 * Class constructor initiating the parsing of the page.
	 */
	public AffluenceParser() {
		Document doc = null;
		mAffluence = "";

		try {
			doc = Jsoup.connect(AFFLUENCE_URL).get();
		} catch (IOException e) {
			System.out.println("Error getting the Document!");
			e.printStackTrace();
		}

		if (doc != null) {
			mAffluence = doc.text();
		}
	}

	/**
	 * Returns the <code>Affluence</code> enum corresponding the its value.
	 * 
	 * @return The enum affluence.
	 */
	public Affluence getAffluence() {
		Affluence a = Affluence.ERROR;

		switch (Integer.valueOf(mAffluence)) {
		case 0:
			a = Affluence.EMPTY;
			break;
		case 1:
			a = Affluence.MEDIUM;
			break;
		case 2:
			a = Affluence.CROWDED;
			break;
		case 3:
			a = Affluence.FULL;
			break;
		case 4:
			a = Affluence.CLOSED;
			break;
		default:
			a = Affluence.ERROR;
			break;
		}

		return a;
	}

}
