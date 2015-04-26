// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportStationSearchRequest" )]
    public sealed class StationSearchRequest
    {
        [ThriftField( 1, true, "stationName" )]
        public string StationName { get; set; }

        [ThriftField( 2, false, "geoPoint" )]
        public GeoPoint UserLocation { get; set; }
    }
}