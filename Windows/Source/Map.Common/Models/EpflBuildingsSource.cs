// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Map.Models
{
    /// <summary>
    /// Helper class to compute the URL of an EPFL building tile using a standard Mercator projection.
    /// </summary>
    public static class EpflBuildingsSource
    {
        // The buildings URL format
        // Parameters are the server number, the level, the zoon level, the X coordinate and the Y coordinate
        private const string Url = "http://plan-epfl-tile{0}.epfl.ch/batiments{1}-merc/{2}/{3:000/000/000}/{4:000/000/000}.png";
        private const int ServerCount = 5;
        private static uint _server = 0;

        /// <summary>
        /// Gets an Uri for the tile at the specified X/Y coordinates, zoom level and buildings level.
        /// </summary>
        public static Uri GetUri( int buildingsLevel, int x, int y, int zoomLevel )
        {
            y = FixY( y, zoomLevel );
            _server = ( _server + 1 ) % ServerCount;
            return new Uri( string.Format( Url, _server, buildingsLevel, zoomLevel, x, y ), UriKind.Absolute );
        }

        /// <summary>
        /// This is required, for some reason.
        /// </summary>
        private static int FixY( int y, int zoom )
        {
            return (int) Math.Floor( 4194303 / Math.Pow( 2, 22 - zoom ) ) - y;
        }
    }
}