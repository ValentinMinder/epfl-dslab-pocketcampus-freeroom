// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportTrip" )]
    public sealed class Trip
    {
        [ThriftField( 3, true, "departureTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime DepartureTime { get; set; }

        [ThriftField( 4, true, "arrivalTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime ArrivalTime { get; set; }

        [ThriftField( 5, true, "from" )]
        public Station Departure { get; set; }

        [ThriftField( 6, true, "to" )]
        public Station Arrival { get; set; }

        [ThriftField( 7, false, "parts" )]
        public Connection[] Connections { get; set; }
    }
}