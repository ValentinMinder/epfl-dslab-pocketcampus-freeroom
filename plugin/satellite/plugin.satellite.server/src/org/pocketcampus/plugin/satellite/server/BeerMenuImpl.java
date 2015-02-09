package org.pocketcampus.plugin.satellite.server;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.satellite.shared.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets Satellite's beer menu from their XML feed.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class BeerMenuImpl implements BeerMenu {
	private static final String BEER_LIST_URL = "https://satellite.bar/pocket/flux.xml";

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
	public BeersResponse get() {
		String xml;
		try {
			xml = _client.get(BEER_LIST_URL, Charset.forName("UTF-8"));
		} catch (Exception e) {
			return new BeersResponse(SatelliteStatusCode.NETWORK_ERROR);
		}

		XElement root = XElement.parse(xml);

		Map<SatelliteBeerContainer, SatelliteMenuPart> menu = new HashMap<SatelliteBeerContainer, SatelliteMenuPart>();

		for (XElement beerElem : root.children(BEER_ELEMENT)) {
			SatelliteBeer beer = new SatelliteBeer();

			beer.setName(beerElem.child(BEER_NAME_ELEMENT).text());
			beer.setBreweryName(beerElem.child(BEER_BREWERY_ELEMENT).text());
			beer.setOriginCountry(beerElem.child(BEER_ORIGIN_ELEMENT).text());
			beer.setDescription(beerElem.child(BEER_DESCRIPTION_ELEMENT).text());

			String alcoholRate = beerElem.child(BEER_ALCOHOL_RATE_ELEMENT).text();
			alcoholRate = alcoholRate.replace(ALCOHOL_RATE_SUFFIX, "");
			beer.setAlcoholRate(Double.parseDouble(alcoholRate));

			String price = beerElem.child(BEER_PRICE_ELEMENT).text();
			price = price.replace(PRICE_SUFFIX, "");
			beer.setPrice(Double.parseDouble(price));

			String containerName = beerElem.attribute(BEER_CONTAINER_ATTRIBUTE);
			boolean isBeerOfTheMonth = containerName.startsWith(CONTAINER_BEER_OF_THE_MONTH_PREFIX);
			if (isBeerOfTheMonth) {
				containerName = containerName.substring(CONTAINER_BEER_OF_THE_MONTH_PREFIX.length());
			}
			SatelliteBeerContainer container = CONTAINERS.get(containerName);
			String beerType = prettify(beerElem.child(BEER_TYPE_ELEMENT).text());

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