// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Device.Location;
using PocketCampus.Common;

namespace PocketCampus.Map
{
    /// <summary>
    /// Converts GeoPositions to GeoCoordinates and vice-versa.
    /// </summary>
    public sealed class GeoPositionToGeoCoordinateConverter : ValueConverter<GeoPosition, GeoCoordinate>
    {
        public override GeoCoordinate Convert( GeoPosition value )
        {
            if ( value == null )
            {
                return new GeoCoordinate( 0, 0 );
            }
            return new GeoCoordinate( value.Latitude, value.Longitude );
        }

        public override GeoPosition ConvertBack( GeoCoordinate value )
        {
            if ( value == null )
            {
                return null;
            }
            return new GeoPosition( value.Latitude, value.Longitude );
        }
    }
}