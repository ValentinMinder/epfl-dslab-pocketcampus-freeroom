// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportTripSearchRequest" )]
    public sealed class TripSearchRequest
    {
        [ThriftField( 1, true, "fromStation" )]
        public Station From { get; set; }

        [ThriftField( 2, true, "toStation" )]
        public Station To { get; set; }
    }
}