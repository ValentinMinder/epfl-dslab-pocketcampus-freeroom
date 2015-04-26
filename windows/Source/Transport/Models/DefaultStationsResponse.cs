// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportDefaultStationsResponse" )]
    public sealed class DefaultStationsResponse
    {
        [ThriftField( 1, false, "stations" )]
        public Station[] Stations { get; set; }

        [ThriftField( 2, true, "statusCode" )]
        public TransportStatus Status { get; set; }
    }
}