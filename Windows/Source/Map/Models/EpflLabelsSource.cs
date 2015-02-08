// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;

namespace PocketCampus.Map.Models
{
    public static class EpflLabelsSource
    {
        // The label URL format
        // Parameters are (in Swiss coords) minX,minY,maxX,maxY, the width, the height, the two-letter language code, and the floor or 'all'
        private const string Url =
            "http://plan.epfl.ch/wms_themes?FORMAT=image/png&LOCALID=-1&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG:21781&BBOX={0},{1},{2},{3}&WIDTH={4}&HEIGHT={5}&LAYERS=locaux_labels_{6}{7},batiments_routes_labels";
        // The available languages, and the default one if the user's isn't available
        private static readonly string[] AvailableLanguages = { "fr", "en" };
        private const string DefaultLanguage = "en";
        private const int FloorLevelAll = 9;

        private static readonly string Language;

        static EpflLabelsSource()
        {
            Language = CultureInfo.CurrentCulture.TwoLetterISOLanguageName;
            if ( !AvailableLanguages.Contains( Language ) )
            {
                Language = DefaultLanguage;
            }
        }

        public static Uri GetUri( double left, double top, double right, double bottom, int zoom, int floor, int width, int height )
        {
            Wgs84ToLv03( ref top, ref left );
            Wgs84ToLv03( ref bottom, ref right );
            return new Uri( string.Format( CultureInfo.InvariantCulture, Url, left, bottom, right, top, width, height, Language, floor >= FloorLevelAll ? "all" : floor.ToString() ) );
        }

        /// <summary>
        /// Adapted from http://www.swisstopo.admin.ch/internet/swisstopo/fr/home/products/software/products/skripts.html
        /// </summary>
        private static void Wgs84ToLv03( ref double latitude, ref double longitude )
        {
            // Converts degrees dec to sex
            latitude = DecToSexAngle( latitude );
            longitude = DecToSexAngle( longitude );

            // Converts degrees to seconds (sex)
            latitude = SexAngleToSeconds( latitude );
            longitude = SexAngleToSeconds( longitude );

            // Axiliary values (% Bern)
            double latAux = ( latitude - 169028.66 ) / 10000;
            double lngAux = ( longitude - 26782.5 ) / 10000;

            // Process X
            latitude = 200147.07
                + 308807.95 * latAux
                + 3745.25 * Math.Pow( lngAux, 2 )
                + 76.63 * Math.Pow( latAux, 2 )
                - 194.56 * Math.Pow( lngAux, 2 ) * latAux
                + 119.79 * Math.Pow( latAux, 3 );

            // Process Y
            longitude = 600072.37
                + 211455.93 * lngAux
                - 10938.51 * lngAux * latAux
                - 0.36 * lngAux * Math.Pow( latAux, 2 )
                - 44.54 * Math.Pow( lngAux, 3 );
        }

        // Convert decimal angle (degrees) to sexagesimal angle (degrees, minutes and seconds dd.mmss,ss)
        private static double DecToSexAngle( double dec )
        {
            double deg = Math.Floor( dec );
            double min = Math.Floor( ( dec - deg ) * 60 );
            double sec = ( ( ( dec - deg ) * 60 ) - min ) * 60;

            return deg + min / 100 + sec / 10000;
        }

        // Convert sexagesimal angle (degrees, minutes and seconds dd.mmss,ss) to seconds
        private static double SexAngleToSeconds( double dms )
        {
            double deg = Math.Floor( dms );
            double min = Math.Floor( ( dms - deg ) * 100 );
            double sec = ( ( ( dms - deg ) * 100 ) - min ) * 100;

            return sec + min * 60 + deg * 3600;
        }
    }
}