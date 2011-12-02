package org.pocketcampus.plugin.satellite.server.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.pocketcampus.plugin.satellite.shared.Beer;

public class BeerParser {
	private String BEER_URL = "http://sat.epfl.ch/";
	private String mBeerName;
	private String mBeerDescription;
	private String mBeerPictureLink;
	
	private Beer mBeer;
	private Document mDoc;

	public BeerParser() {
		mBeerName = "";
		mBeerDescription = "";
		mBeerPictureLink = "";

		mDoc = null;
	}
	
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
							mBeerPictureLink = img.getElementsByAttribute("src").attr("src");
							Elements brr = img.getAllElements();
							Node br  = brr.last();
							
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
//								System.out.println("Description : " + mBeerDescription);
							}
							
							
//							Element img = h4.nextElementSibling();
//							mBeerPictureLink = img.attr("src").trim();
//
//							Element br = img.nextElementSibling();
//
//							while (br.nextElementSibling() != null
//									&& br.nextElementSibling().outerHtml()
//											.equals(br.outerHtml())) {
//
//								Node n = br.nextSibling();
//
//								if (n != null) {
//									String nText = n.toString();
//									if (nText.length() > 2) {
//										mBeerDescription = mBeerDescription
//												.concat(n.toString().trim()
//														+ "\n");
//									}
//									br = br.nextElementSibling();
//								}
//								mBeerDescription = mBeerDescription;
//							}
						}
					}
				}
			}
		}
		cleantexts();
		mBeer = new Beer((mBeerName+mBeerDescription).hashCode(), mBeerName, mBeerDescription);
		mBeer.setPictureUrl(mBeerPictureLink);
	}
	
	private void cleantexts() {
		mBeerName = mBeerName.replace("&agrave;", "�");
		mBeerName = mBeerName.replace("&eacute", "�");
		mBeerName = mBeerName.replace("&egrave;", "�");
		mBeerName = mBeerName.replace("&ucirc;", "�");
		mBeerName = mBeerName.replace("&deg", "�");
		mBeerName = mBeerName.replace(";", "");
		
		mBeerDescription = mBeerDescription.replace("&agrave;", "�");
		mBeerDescription = mBeerDescription.replace("&eacute", "�");
		mBeerDescription = mBeerDescription.replace("&egrave;", "�");
		mBeerDescription = mBeerDescription.replace("&ucirc;", "�");
		mBeerDescription = mBeerDescription.replace("&deg", "�");
		mBeerDescription = mBeerDescription.replace(";", "");
	}
	
	public Beer getBeerOfMonth() {
		return mBeer;
	}

}
