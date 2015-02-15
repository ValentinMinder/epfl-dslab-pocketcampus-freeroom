package org.pocketcampus.plugin.map.server;

/**
 * Used to convert coordinates
 */
public class CoordinateConverter {
    private static final double A = 6378137;

    public static double convertEPSG4326ToLat(double y) {
        double lat = (Math.PI / 2.0 - 2.0 * Math.atan(Math.exp(-y / A)));
        lat = lat / Math.PI * 180;
        return lat;
    }

    public static double convertEPSG4326ToLon(double x) {
        double lon = adjust_lon(x / A);
        lon = lon / Math.PI * 180;
        return lon;
    }

    private static double adjust_lon(double x) {
        x = (Math.abs(x) < Math.PI) ? x : (x - (Double.compare(x, 0) * 2 * Math.PI));
        return x;
    }
}