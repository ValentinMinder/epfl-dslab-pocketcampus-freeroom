// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    /// <summary>
    /// Response for a trips request.
    /// </summary>
    [ThriftStruct( "QueryTripsResult" )]
    public sealed class TripsResult
    {
        /// <summary>
        /// The trips.
        /// </summary>
        [ThriftField( 9, true, "connections" )]
        public Trip[] Trips { get; set; }
    }
}