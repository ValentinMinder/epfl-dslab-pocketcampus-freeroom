package org.pocketcampus.plugin.map.android;

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
 * @author Florian <florian.laurent@epfl.ch>
 * @author Johan <johan.leuenberger@epfl.ch>
 *
 */
public class EpflTileSource extends OnlineTileSourceBase {
	private OnlineTileSourceBase outsideEpflTileSource_ = TileSourceFactory.MAPNIK;

	private final static int MIN_ZOOM = 14;
	private final static int MAX_ZOOM = 19;
	private final static int TILE_SIZE_PX = 256;
	private final static String TILE_IMAGE_EXTENSION = ".png";
	
	/**
	 * Creates a new tile source for the EPFL map
	 * @param level the level to display (-4 to 8, and all-merc)
	 */
	public EpflTileSource(final String level) {
		super("EPFL" + level, ResourceProxy.string.mapnik, MIN_ZOOM, MAX_ZOOM, TILE_SIZE_PX, TILE_IMAGE_EXTENSION
				,"http://plan-epfl-tile0.epfl.ch/batiments" + level + "/"
				,"http://plan-epfl-tile1.epfl.ch/batiments" + level + "/"
				,"http://plan-epfl-tile2.epfl.ch/batiments" + level + "/"
				,"http://plan-epfl-tile3.epfl.ch/batiments" + level + "/"
				,"http://plan-epfl-tile4.epfl.ch/batiments" + level + "/"
				);
		
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
				+ decomp(ycoord(aTile.getY(), aTile.getZoomLevel()))
				+ TILE_IMAGE_EXTENSION;

		if (checkUrl(url)) {
			return url;
		} else {
			System.out.println("=> " + url + " failed.");
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
	 * @param a the integer to decompose
	 * @return the formatted string.
	 */
	private String decomp(int a) {
		String s1 = padString(a, 9);
		String s = s1.substring(0, 3) + "/" + s1.substring(3, 6) + "/"
				+ s1.substring(6, 9);
		return s;
	}
	
	/**
	 * Formats a number as a String of nbChar characters,
	 * padding it with 0.
	 * @param number
	 * @param nbChar
	 * @return
	 */
	public static String padString(int number, int nbChar) {
		return String.format("%0" + nbChar + "d", number);
	}
	
	int ycoord(int y, int zoom) {
		return (int) Math.floor(4194303 / (Math.pow(2, (22 - zoom)))) - y;
	}

}
