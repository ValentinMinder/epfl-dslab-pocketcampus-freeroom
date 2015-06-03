// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportGeoPoint" )]
    public sealed class GeoPoint
    {
        [ThriftField( 1, true, "latitude" )]
        public double Latitude { get; set; }

        [ThriftField( 2, true, "longitude" )]
        public double Longitude { get; set; }
    }
}