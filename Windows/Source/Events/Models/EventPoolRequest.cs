// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Request for an event pool.
    /// </summary>
    [ThriftStruct( "EventPoolRequest" )]
    public sealed class EventPoolRequest
    {
        /// <summary>
        /// The requested pool's ID.
        /// </summary>
        [ThriftField( 1, true, "eventPoolId" )]
        public long PoolId { get; set; }

        /// <summary>
        /// The user's tickets that may grant them access to the pool if necessary.
        /// </summary>
        [ThriftField( 3, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        /// <summary>
        /// The favorite events.
        /// </summary>
        /// <remarks>
        /// The server ignores the ones not in the pool.
        /// </remarks>
        [ThriftField( 4, false, "starredEventItems" )]
        public long[] FavoriteEventIds { get; set; }

        /// <summary>
        /// The requested language.
        /// </summary>
        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }

        /// <summary>
        /// The period of time, in days, in which to search for events.
        /// </summary>
        [ThriftField( 8, false, "periodInHours" )]
        public int? HoursCount { get; set; }

        /// <summary>
        /// A value indicating whether events should be searched for in the past.
        /// </summary>
        [ThriftField( 7, false, "fetchPast" )]
        public bool? IsInPast { get; set; }
    }
}