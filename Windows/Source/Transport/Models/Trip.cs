// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    /// <summary>
    /// Trip from a station to another, possibly via other stations.
    /// </summary>
    [ThriftStruct( "TransportTrip" )]
    public sealed class Trip
    {
        /// <summary>
        /// The trip's departure time.
        /// </summary>
        [ThriftField( 3, true, "departureTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime DepartureTime { get; set; }

        /// <summary>
        /// The trip's arrival time.
        /// </summary>
        [ThriftField( 4, true, "arrivalTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime ArrivalTime { get; set; }

        /// <summary>
        /// The trip's departure station.
        /// </summary>
        [ThriftField( 5, true, "from" )]
        public Station Departure { get; set; }

        /// <summary>
        /// The trip's arrival station.
        /// </summary>
        [ThriftField( 6, true, "to" )]
        public Station Arrival { get; set; }

        /// <summary>
        /// The trip's connections.
        /// </summary>
        /// <remarks>
        /// At least one.
        /// </remarks>
        [ThriftField( 7, false, "parts" )]
        public Connection[] Connections { get; set; }
    }
}