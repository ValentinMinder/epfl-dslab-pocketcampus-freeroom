package org.pocketcampus.plugin.satellite.server;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.pocketcampus.platform.sdk.shared.HttpClient;
import org.pocketcampus.plugin.satellite.shared.*;

public final class BeerListImpl implements BeerList {
	private static final String BEER_LIST_URL = "http://sat.epfl.ch/pocket/flux.xml";

	private static final String BEER_ELEMENT = "biere";
	private static final String BEER_CONTAINER_ATTRIBUTE = "contenant";
	private static final String BEER_NAME_ELEMENT = "nom";
	private static final String BEER_BREWERY_ELEMENT = "brasserie";
	private static final String BEER_TYPE_ELEMENT = "type";
	private static final String BEER_ORIGIN_ELEMENT = "origine";
	private static final String BEER_ALCOHOL_RATE_ELEMENT = "teneur";
	private static final String BEER_PRICE_ELEMENT = "prix";
	private static final String BEER_DESCRIPTION_ELEMENT = "description";

	private static final String CONTAINER_BEER_OF_THE_MONTH_PREFIX = "mois_";
	private static final String ALCOHOL_RATE_SUFFIX = "°";
	private static final String PRICE_SUFFIX = "CHF";

	private static final Map<String, SatelliteBeerContainer> CONTAINERS = new HashMap<String, SatelliteBeerContainer>();

	static {
		CONTAINERS.put("pression", SatelliteBeerContainer.DRAFT);
		CONTAINERS.put("bouteille", SatelliteBeerContainer.SMALL_BOTTLE);
		CONTAINERS.put("grande_bouteille", SatelliteBeerContainer.LARGE_BOTTLE);
	}

	private final HttpClient _client;

	public BeerListImpl(HttpClient client) {
		_client = client;
	}

	@Override
	public List<SatelliteBeer> get() throws Exception {
		String xml = _client.getString(BEER_LIST_URL, StandardCharsets.UTF_8);

		Element xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes()))
				.getDocumentElement();

		List<SatelliteBeer> beers = new ArrayList<SatelliteBeer>();
		for (Node beerNode : getNodes(xdoc, BEER_ELEMENT)) {
			SatelliteBeer beer = new SatelliteBeer();

			beer.setName(getChildText(beerNode, BEER_NAME_ELEMENT));
			beer.setBreweryName(getChildText(beerNode, BEER_BREWERY_ELEMENT));
			beer.setBeerType(getChildText(beerNode, BEER_TYPE_ELEMENT));
			beer.setOriginCountry(getChildText(beerNode, BEER_ORIGIN_ELEMENT));
			beer.setDescription(getChildText(beerNode, BEER_DESCRIPTION_ELEMENT));

			String container = getAttributeText(beerNode, BEER_CONTAINER_ATTRIBUTE);
			beer.setBeerOfTheMonth(container.startsWith(CONTAINER_BEER_OF_THE_MONTH_PREFIX));
			if (beer.isBeerOfTheMonth()) {
				container = container.substring(CONTAINER_BEER_OF_THE_MONTH_PREFIX.length());
			}

			String alcoholRate = getChildText(beerNode, BEER_ALCOHOL_RATE_ELEMENT);
			alcoholRate = alcoholRate.replace(ALCOHOL_RATE_SUFFIX, "");
			beer.setAlcoholRate(Double.parseDouble(alcoholRate));

			String price = getChildText(beerNode, BEER_PRICE_ELEMENT);
			price = price.replace(PRICE_SUFFIX, "");
			beer.setPrice(Double.parseDouble(price));

			beers.add(beer);
		}

		return beers;
	}

	/** Gets the text from the specified attribute of the specified XML node. */
	private static String getAttributeText(Node node, String elementName) {
		return ((Element) node).getAttribute(elementName).trim();
	}

	/** Gets the text from the specified child of the specified XML node. */
	private static String getChildText(Node node, String elementName) {
		return ((Element) node).getElementsByTagName(elementName).item(0).getTextContent().trim();
	}

	/** Like getElementsByTagName, but returns an iterable class instead of a NodeList. */
	private static List<Node> getNodes(Element parent, String name) {
		NodeList nodes = parent.getElementsByTagName(name);
		List<Node> retVal = new ArrayList<Node>(nodes.getLength());
		for (int n = 0; n < nodes.getLength(); n++) {
			retVal.add(nodes.item(n));
		}
		return retVal;
	}
}