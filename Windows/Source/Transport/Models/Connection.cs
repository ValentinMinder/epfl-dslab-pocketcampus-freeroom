// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    /// <summary>
    /// Part of a trip, from one station to another.
    /// </summary>
    [ThriftStruct( "TransportConnection" )]
    public sealed class Connection
    {
        /// <summary>
        /// The connection's departure station.
        /// </summary>
        [ThriftField( 1, true, "departure" )]
        public Station Departure { get; set; }

        /// <summary>
        /// The connection's arrival station.
        /// </summary>
        [ThriftField( 2, true, "arrival" )]
        public Station Arrival { get; set; }

        /// <summary>
        /// The connection's line.
        /// </summary>
        [ThriftField( 4, false, "line" )]
        public Line Line { get; set; }

        /// <summary>
        /// The connection's departure time.
        /// </summary>
        [ThriftField( 6, false, "departureTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? DepartureTime { get; set; }

        /// <summary>
        /// The connection's departure position, if any.
        /// </summary>
        /// <remarks>
        /// This is usually the platform number.
        /// </remarks>
        [ThriftField( 7, false, "departurePosition" )]
        public string DeparturePosition { get; set; }

        /// <summary>
        /// The connection's arrival time.
        /// </summary>
        [ThriftField( 8, false, "arrivalTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? ArrivalTime { get; set; }

        /// <summary>
        /// The connection's arrival position, if any.
        /// </summary>
        /// <remarks>
        /// This is usually the platform number.
        /// </remarks>
        [ThriftField( 9, false, "arrivalPosition" )]
        public string ArrivalPosition { get; set; }


        /// <summary>
        /// Whether the connection is on foot.
        /// </summary>
        /// <remarks>
        /// If so, there is no line.
        /// </remarks>
        [ThriftField( 11, true, "foot" )]
        public bool IsOnFoot { get; set; }
    }
}