package org.pocketcampus.plugin.satellite.server.parse;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.pocketcampus.plugin.satellite.shared.Beer;

/**
 * A class to parse the beer of the month on the satellite web site.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class BeerParser {
	/** The url of the web site page we want to parse */
	private String BEER_URL = "http://sat.epfl.ch/";
	/** The beer name */
	private String mBeerName;
	/** The beer description */
	private String mBeerDescription;
	/** The beer's picture link */
	private String mBeerPictureLink;
	/** The beer we're will return to the server */
	private Beer mBeer;
	/** The document we get from the web site and that will be parsed */
	private Document mDoc;

	/**
	 * The constructor that initialize data.
	 */
	public BeerParser() {
		mBeerName = "";
		mBeerDescription = "";
		mBeerPictureLink = "";

		mDoc = null;
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
			divs = mDoc.select("div.text_block_text");

			if (divs != null && !divs.isEmpty()) {
				for (Element div : divs) {
					Elements h4s = div.select("h4");

					if (h4s != null && !h4s.isEmpty()) {
						for (Element h4 : h4s) {
							int indexOfDoublePoints = h4.text().indexOf(":");
							mBeerName = h4
									.text()
									.substring(indexOfDoublePoints + 1,
											h4.text().length()).trim();
							Element img = h4.nextElementSibling();
							mBeerPictureLink = img
									.getElementsByAttribute("src").attr("src");
							Elements brr = img.getAllElements();
							Node br = brr.last();

							while (br != null && br.nextSibling() != null) {
								Node n = br.nextSibling();
								if (n != null) {
									String nText = n.toString();
									if (nText.length() > 2) {
										mBeerDescription = mBeerDescription
												.concat(n.toString().trim()
														+ "\n");
									}
									br = br.nextSibling();
								}
								mBeerDescription = mBeerDescription.trim();
							}
							// Old parser (November)
							/*
							 * Element img = h4.nextElementSibling();
							 * mBeerPictureLink = img.attr("src").trim();
							 * 
							 * Element br = img.nextElementSibling();
							 * 
							 * while (br.nextElementSibling() != null &&
							 * br.nextElementSibling().outerHtml()
							 * .equals(br.outerHtml())) {
							 * 
							 * Node n = br.nextSibling();
							 * 
							 * if (n != null) { String nText = n.toString(); if
							 * (nText.length() > 2) { mBeerDescription =
							 * mBeerDescription .concat(n.toString().trim() +
							 * "\n"); } br = br.nextElementSibling(); }
							 * mBeerDescription = mBeerDescription; }
							 */
						}
					}
				}
			}
		}
		cleantexts();
		mBeer = new Beer((mBeerName + mBeerDescription).hashCode(), mBeerName,
				mBeerDescription);
		mBeer.setPictureUrl(mBeerPictureLink);
	}

	/**
	 * Cleans the beer name and description to get rid of the malformed
	 * characters.
	 */
	private void cleantexts() {
		mBeerName = StringEscapeUtils.unescapeHtml4(mBeerName);
		mBeerDescription = StringEscapeUtils.unescapeHtml4(mBeerDescription);
	}

	/**
	 * Method called by the server to get the beer of the month.
	 * 
	 * @return
	 */
	public Beer getBeerOfMonth() {
		return mBeer;
	}
}
