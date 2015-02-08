// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using Windows.Devices.Geolocation;

namespace PocketCampus.Map
{
    // Convert our GeoPosition class to the WinRT Geopoint
    public sealed class GeoPositionToGeopointConverter : ValueConverter<GeoPosition, Geopoint>
    {
        public override Geopoint Convert( GeoPosition value )
        {
            if ( value == null )
            {
                return new Geopoint( new BasicGeoposition() );
            }
            return new Geopoint( new BasicGeoposition { Latitude = value.Latitude, Longitude = value.Longitude } );
        }

        public override GeoPosition ConvertBack( Geopoint value )
        {
            return new GeoPosition( value.Position.Latitude, value.Position.Longitude );
        }
    }
}