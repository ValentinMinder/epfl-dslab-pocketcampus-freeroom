// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Map.Models
{
    public static class EpflBuildingsSource
    {
        // The buildings URL format
        // Parameters are the server number, the floor or "all", the zoom level, the X coordinate and the Y coordinate
        private const string Url = "http://plan-epfl-tile{0}.epfl.ch/batiments{1}-merc/{2}/{3:000/000/000}/{4:000/000/000}.png";
        private const int FloorLevelAll = 9;
        private const int ServerCount = 5;
        private static uint _server;


        public static Uri GetUri( int x, int y, int zoomLevel, int floor )
        {
            y = FixY( y, zoomLevel );
            _server = ( _server + 1 ) % ServerCount;
            return new Uri( string.Format( Url, _server, floor >= FloorLevelAll ? "all" : floor.ToString(), zoomLevel, x, y ), UriKind.Absolute );
        }

        // HACK: Required for some reason...
        private static int FixY( int y, int zoom )
        {
            return (int) Math.Floor( 4194303 / Math.Pow( 2, 22 - zoom ) ) - y;
        }
    }
}