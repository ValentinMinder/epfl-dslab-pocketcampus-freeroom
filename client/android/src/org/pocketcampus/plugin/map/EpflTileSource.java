package org.pocketcampus.plugin.map;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

/**
 * Tile source of EPFL map for OSMDroid
 * 
 * @author Johan
 *
 */
public class EpflTileSource extends OnlineTileSourceBase {
	private OnlineTileSourceBase outsideEpflTileSource_ = TileSourceFactory.MAPNIK;

	private final static int MIN_ZOOM = 0;
	private final static int MAX_ZOOM = 18;
	private final static int TILE_SIZE_PX = 256;
	private final static String TILE_IMAGE_TYPE = ".png";
	
	/**
	 * Creates a new tile source for the EPFL map
	 * @param level the level to display (-4 to 8, and all-merc)
	 */
	public EpflTileSource(final String level) {
		super("EPFL" + level, ResourceProxy.string.mapnik, MIN_ZOOM, MAX_ZOOM, TILE_SIZE_PX, TILE_IMAGE_TYPE,
				"http://plan-epfl-tile0.epfl.ch/batiments" + level + "/",
				"http://plan-epfl-tile1.epfl.ch/batiments" + level + "/",
				"http://plan-epfl-tile2.epfl.ch/batiments" + level + "/",
				"http://plan-epfl-tile3.epfl.ch/batiments" + level + "/",
				"http://plan-epfl-tile4.epfl.ch/batiments" + level + "/");
		
	}
	
	/**
	 * Creates a new tile source for the EPFL map (level=all)
	 */
	public EpflTileSource() {
		this("all-merc");
	}
	
	@Override
	public String getTileURLString(MapTile aTile) {
		String url = getBaseUrl() + aTile.getZoomLevel() + "/"
				+ decomp(aTile.getX()) + "/"
				+ decomp(ycoord(aTile.getY(), aTile.getZoomLevel())) + ".png";

		if (checkUrl(url)) {
			System.out.println("FOUND: " + url);
			return url;
		} else {
			return outsideEpflTileSource_.getTileURLString(aTile);
		}

	}

	//XXX Find a faster way to do that...
	/**
	 * Checks that the URL exists
	 * @param url the url to check.
	 * @return true if the url exists, false otherwise.
	 */
	private boolean checkUrl(String url) {
		URL u = null;

		try {
			u = new URL(url);
			URLConnection connection = u.openConnection();
			int code = ((HttpURLConnection) connection).getResponseCode();

			if (code == 200) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}

		return false;
	}

	/**
	 * Splits the integer into the following format -> NNN/NNN/NNN
	 * XXX see utils.StringUtils
	 * @param a the integer to decompose
	 * @return the formatted string.
	 */
	private String decomp(int a) {
		String s1 = pad(a);
		String s = s1.substring(0, 3) + "/" + s1.substring(3, 6) + "/"
				+ s1.substring(6, 9);
		return s;
	}

	/**
	 * Adds leading zero to a number
	 * @param a an integer
	 * @return the padded number.
	 */
	private String pad(int a) {
		return String.format("%09d", a); 
	}

	int ycoord(int y, int zoom) {
		return (int) Math.floor(4194303 / (Math.pow(2, (22 - zoom)))) - y;
	}

}
