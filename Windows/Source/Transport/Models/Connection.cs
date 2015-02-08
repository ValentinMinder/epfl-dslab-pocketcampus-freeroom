// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportConnection" )]
    public sealed class Connection
    {
        [ThriftField( 1, true, "departure" )]
        public Station Departure { get; set; }

        [ThriftField( 2, true, "arrival" )]
        public Station Arrival { get; set; }

        [ThriftField( 4, false, "line" )]
        public Line Line { get; set; }

        [ThriftField( 6, false, "departureTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? DepartureTime { get; set; }

        [ThriftField( 7, false, "departurePosition" )]
        public string DeparturePosition { get; set; }

        [ThriftField( 8, false, "arrivalTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? ArrivalTime { get; set; }

        [ThriftField( 9, false, "arrivalPosition" )]
        public string ArrivalPosition { get; set; }

        // If true, Line is null.
        [ThriftField( 11, true, "foot" )]
        public bool IsOnFoot { get; set; }
    }
}