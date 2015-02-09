package org.pocketcampus.plugin.satellite.server.old;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pocketcampus.plugin.satellite.shared.Beer;

import java.io.IOException;

/**
 * A class to parse the beer of the month on the Satellite web site.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class BeerParser {
	/** The URL of the web site page we want to parse. */
	private String BEER_URL = "http://satellite.bar/pocket/flux.xml";
	/** The document we get from the web site and that will be parsed. */
	private Document mDoc;
	/** The beer name. */
	private String mBeerName;
	/** The beer description. */
	private String mBeerDescription;
	/** The beer's picture link. */
	private String mBeerPictureLink;
	/** The beer we will return to the server. */
	private Beer mBeer;

	/**
	 * Class constructor initializing data.
	 */
	public BeerParser() {
		mDoc = null;
		mBeerName = "";
		mBeerDescription = "";
		mBeerPictureLink = "";
	}

	/**
	 * Parsing method. Gets the beer name, description and picture link from the
	 * web page.
	 */
	public void parse() {
		Elements divs = null;
		try {
			mDoc = Jsoup.connect(BEER_URL).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mDoc != null) {
			divs = mDoc.select("biere");

			if (divs != null && !divs.isEmpty()) {
				for (Element div : divs) {

					if (div.attributes().toString()
							.equals(" contenant=\"mois_pression\"")) {
						mBeerName = div.select("nom").text();
						mBeerDescription = div.select("description").text();
					}

				}
			}
		}

		mBeer = new Beer((mBeerName + mBeerDescription).hashCode(), mBeerName,
				mBeerDescription);
		mBeer.setPictureUrl(mBeerPictureLink);
	}

	/**
	 * Called by the server to get the beer of the month.
	 * 
	 * @return The beer of the month.
	 */
	public Beer getBeerOfMonth() {
		return mBeer;
	}
}
