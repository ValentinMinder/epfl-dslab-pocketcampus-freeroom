// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportStation" )]
    public sealed class Station
    {
        [ThriftField( 2, true, "id" )]
        public int Id { get; set; }

        [ThriftField( 3, false, "latitude" )]
        [ThriftConverter( typeof( HafasCoordinateConverter ) )]
        public double? Latitude { get; set; }

        [ThriftField( 4, false, "longitude" )]
        [ThriftConverter( typeof( HafasCoordinateConverter ) )]
        public double? Longitude { get; set; }

        [ThriftField( 6, false, "name" )]
        public string Name { get; set; }


        public GeoPosition Position
        {
            get { return new GeoPosition( Latitude.Value, Longitude.Value ); }
        }
    }
}