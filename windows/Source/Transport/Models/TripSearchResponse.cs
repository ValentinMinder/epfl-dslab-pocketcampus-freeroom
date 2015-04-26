// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportTripSearchResponse" )]
    public sealed class TripSearchResponse
    {
        [ThriftField( 1, false, "trips" )]
        public Trip[] Trips { get; set; }

        [ThriftField( 2, true, "statusCode" )]
        public TransportStatus Status { get; set; }
    }
}