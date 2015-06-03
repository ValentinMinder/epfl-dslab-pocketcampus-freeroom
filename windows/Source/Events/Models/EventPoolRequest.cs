// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventPoolRequest" )]
    public sealed class EventPoolRequest
    {
        [ThriftField( 1, true, "eventPoolId" )]
        public long PoolId { get; set; }

        [ThriftField( 3, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        // The server ignores the ones not in the pool.
        [ThriftField( 4, false, "starredEventItems" )]
        public long[] FavoriteEventIds { get; set; }

        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }

        [ThriftField( 8, false, "periodInHours" )]
        public int? HoursCount { get; set; }

        [ThriftField( 7, false, "fetchPast" )]
        public bool? IsInPast { get; set; }
    }
}