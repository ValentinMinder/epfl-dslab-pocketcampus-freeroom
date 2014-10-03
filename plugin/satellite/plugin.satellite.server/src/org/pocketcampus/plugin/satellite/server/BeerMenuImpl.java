package org.pocketcampus.plugin.satellite.server;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.plugin.satellite.shared.*;

/**
 * Gets Satellite's beer menu from their XML feed.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class BeerMenuImpl implements BeerMenu {
	private static final String BEER_LIST_URL = "http://satellite.bar/pocket/flux.xml";

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
		CONTAINERS.put("bouteille", SatelliteBeerContainer.BOTTLE);
		CONTAINERS.put("grande_bouteille", SatelliteBeerContainer.LARGE_BOTTLE);
	}

	private final HttpClient _client;

	public BeerMenuImpl(HttpClient client) {
		_client = client;
	}

	@Override
	public BeersResponse get() throws Exception {
		String xml;
		try {
			xml = _client.get(BEER_LIST_URL, Charset.forName("UTF-8"));
		} catch (Exception e) {
			return new BeersResponse(SatelliteStatusCode.NETWORK_ERROR);
		}

		Element xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes()))
				.getDocumentElement();

		Map<SatelliteBeerContainer, SatelliteMenuPart> menu = new HashMap<SatelliteBeerContainer, SatelliteMenuPart>();

		for (Node beerNode : getNodes(xdoc, BEER_ELEMENT)) {
			SatelliteBeer beer = new SatelliteBeer();

			beer.setName(getChildText(beerNode, BEER_NAME_ELEMENT));
			beer.setBreweryName(getChildText(beerNode, BEER_BREWERY_ELEMENT));
			beer.setOriginCountry(getChildText(beerNode, BEER_ORIGIN_ELEMENT));
			beer.setDescription(getChildText(beerNode, BEER_DESCRIPTION_ELEMENT));

			String alcoholRate = getChildText(beerNode, BEER_ALCOHOL_RATE_ELEMENT);
			alcoholRate = alcoholRate.replace(ALCOHOL_RATE_SUFFIX, "");
			beer.setAlcoholRate(Double.parseDouble(alcoholRate));

			String price = getChildText(beerNode, BEER_PRICE_ELEMENT);
			price = price.replace(PRICE_SUFFIX, "");
			beer.setPrice(Double.parseDouble(price));

			String containerName = getAttributeText(beerNode, BEER_CONTAINER_ATTRIBUTE);
			boolean isBeerOfTheMonth = containerName.startsWith(CONTAINER_BEER_OF_THE_MONTH_PREFIX);
			if (isBeerOfTheMonth) {
				containerName = containerName.substring(CONTAINER_BEER_OF_THE_MONTH_PREFIX.length());
			}
			SatelliteBeerContainer container = CONTAINERS.get(containerName);
			String beerType = prettify(getChildText(beerNode, BEER_TYPE_ELEMENT));

			if (!menu.containsKey(container)) {
				menu.put(container, new SatelliteMenuPart(new ArrayList<SatelliteBeer>(), new HashMap<String, List<SatelliteBeer>>()));
			}
			if (isBeerOfTheMonth) {
				menu.get(container).addToBeersOfTheMonth(beer);
			} else {
				if (!menu.get(container).getBeers().containsKey(beerType)) {
					menu.get(container).getBeers().put(beerType, new ArrayList<SatelliteBeer>());
				}
				menu.get(container).getBeers().get(beerType).add(beer);
			}
		}

		return new BeersResponse(SatelliteStatusCode.OK).setBeerList(menu);
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

	private static String prettify(String s) {
		String[] split = s.split(" ");
		String result = "";
		for (int n = 0; n < split.length; n++) {
			result += Character.toUpperCase(split[n].charAt(0)) + split[n].substring(1).toLowerCase();
			result += " ";
		}
		return result.trim();
	}
}