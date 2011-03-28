package org.pocketcampus.plugin.map;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.ResourceProxy.string;

public class EpflTileSource extends OnlineTileSourceBase {
	private OnlineTileSourceBase outsideEpflTileSource_ = TileSourceFactory.MAPNIK;

	public EpflTileSource(final String aName, final string aResourceId,
			final int aZoomMinLevel, final int aZoomMaxLevel,
			final int aTileSizePixels, final String aImageFilenameEnding,
			final String... aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding, aBaseUrl);
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

	String decomp(int a) {
		String s1 = pad(a);
		String s = s1.substring(0, 3) + "/" + s1.substring(3, 6) + "/"
				+ s1.substring(6, 9);
		return s;
	}

	String pad(int a) {
		Integer b = a;
		String c = b.toString();

		while (c.length() < 9) {
			c = "0" + c;
		}

		return c;
	}

	int ycoord(int y, int zoom) {
		return (int) Math.floor(4194303 / (Math.pow(2, (22 - zoom)))) - y;
	}

}
