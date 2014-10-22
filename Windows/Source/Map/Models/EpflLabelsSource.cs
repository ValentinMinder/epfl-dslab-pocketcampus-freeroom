using System;
using System.Globalization;
using System.Linq;

namespace PocketCampus.Map.Models
{
    public static class EpflLabelsSource
    {
        // The label URL format
        // Parameters are the bounding box, the width, the height, the two-letter language code, and the floor or 'all'
        private const string Url =
            "http://plan.epfl.ch/wms_themes?FORMAT=image/png&LOCALID=-1&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG:900913&BBOX={0}&WIDTH={1}&HEIGHT={2}&LAYERS=locaux_labels_{3}{4},batiments_routes_labels";
        // The available languages, and the default one if the user's isn't available
        private static readonly string[] AvailableLanguages = { "fr", "en" };
        private const string DefaultLanguage = "en";
        private const int FloorLevelAll = 9;

        public static Uri GetUri( int x, int y, int zoom, int floor, int squareTileSize )
        {
            var boundingBox = GlobalMercator.TileBounds( x, y, zoom, squareTileSize );
            string language = CultureInfo.CurrentCulture.TwoLetterISOLanguageName;
            if ( !AvailableLanguages.Contains( language ) )
            {
                language = DefaultLanguage;
            }
            return new Uri( string.Format( Url, boundingBox, squareTileSize, squareTileSize, language, floor >= FloorLevelAll ? "all" : floor.ToString() ) );
        }


        /// <summary>
        /// From https://github.com/Sumbera/WMSonWin81-UniversalApp/blob/master/WMSOnWin81/WMSOnWin81.Shared/GlobalMercator.cs
        /// A big thanks to this guy. Really.
        /// </summary>
        private static class GlobalMercator
        {
            private const int EarthRadius = 6378137;
            private const double InitialResolution = 2 * Math.PI * EarthRadius;
            private const double OriginShift = 2 * Math.PI * EarthRadius / 2;

            // Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
            private static Point PixelsToMeters( Point p, int zoom, int tileSize )
            {
                var res = Resolution( zoom, tileSize );
                return new Point( p.X * res - OriginShift, p.Y * res - OriginShift );
            }

            // Returns bounds of the given tile in EPSG:900913 coordinates
            public static Rect TileBounds( int x, int y, int zoom, int tileSize )
            {
                var min = PixelsToMeters( new Point( x * tileSize, y * tileSize ), zoom, tileSize );
                var max = PixelsToMeters( new Point( ( x + 1 ) * tileSize, ( y + 1 ) * tileSize ), zoom, tileSize );
                return new Rect( min, max );
            }

            // Resolution (meters/pixel) for given zoom level (measured at Equator)
            private static double Resolution( int zoom, int tileSize )
            {
                return InitialResolution / tileSize / Math.Pow( 2, zoom );
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
                    + "," + Math.Abs( Bottom ).ToString( CultureInfo.InvariantCulture )
                    + "," + Right.ToString( CultureInfo.InvariantCulture )
                    + "," + Math.Abs( Top ).ToString( CultureInfo.InvariantCulture );
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