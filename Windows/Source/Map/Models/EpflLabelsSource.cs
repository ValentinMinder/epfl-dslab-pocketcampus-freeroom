using System;
using System.Globalization;

namespace PocketCampus.Map.Models
{
    public static class EpflLabelsSource
    {
        // The label URL format
        // Parameters are the bounding box, the width, the height, and the comma-separated layers list
        private const string Url =
            "http://plan.epfl.ch/wms_themes?FORMAT=image/png&LOCALID=-1&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG:900913&BBOX={0}&WIDTH={1}&HEIGHT={2}&LAYERS={3}";
        private const string Layers = "locaux_labels_frall,batiments_routes_labels";

        public static Uri GetUri( int x, int y, int zoom, double width, double height )
        {
            var boundingBox = GlobalMercator.TileBounds( x, y, zoom );
            return new Uri( string.Format( Url, boundingBox, width, height, Layers ) );
        }


        /// <summary>
        /// From https://github.com/Sumbera/WMSonWin81-UniversalApp/blob/master/WMSOnWin81/WMSOnWin81.Shared/GlobalMercator.cs
        /// A big thanks to this guy. Really.
        /// </summary>
        private static class GlobalMercator
        {
            private const int TileSize = 256;
            private const int EarthRadius = 6378137;
            private const double InitialResolution = 2 * Math.PI * EarthRadius / TileSize;
            private const double OriginShift = 2 * Math.PI * EarthRadius / 2;

            // Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
            private static Point PixelsToMeters( Point p, int zoom )
            {
                var res = Resolution( zoom );
                return new Point( p.X * res - OriginShift, p.Y * res - OriginShift );
            }

            // Returns bounds of the given tile in EPSG:900913 coordinates
            public static Rect TileBounds( double x, double y, int zoom )
            {
                var min = PixelsToMeters( new Point( x * TileSize, y * TileSize ), zoom );
                var max = PixelsToMeters( new Point( ( x + 1 ) * TileSize, ( y + 1 ) * TileSize ), zoom );
                return new Rect( min, max );
            }

            // Resolution (meters/pixel) for given zoom level (measured at Equator)
            private static double Resolution( int zoom )
            {
                return InitialResolution / Math.Pow( 2, zoom );
            }
        }

        public struct Rect
        {
            public readonly double Top;
            public readonly double Left;
            public readonly double Bottom;
            public readonly double Right;

            public Rect( Point topLeft, Point bottomRight )
            {
                Left = topLeft.X;
                Top = topLeft.Y;
                Right = bottomRight.X;
                Bottom = bottomRight.Y;
            }

            public override string ToString()
            {
                return Left.ToString( CultureInfo.InvariantCulture )
             + "," +
             Math.Abs( Bottom ).ToString( CultureInfo.InvariantCulture )
             + "," +
             Right.ToString( CultureInfo.InvariantCulture )
             + "," +
             Math.Abs( Top ).ToString( CultureInfo.InvariantCulture );
            }
        }

        public struct Point
        {
            public readonly double X;
            public readonly double Y;

            public Point( double x, double y )
            {
                X = x;
                Y = y;
            }
        }
    }
}