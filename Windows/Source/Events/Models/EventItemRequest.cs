// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Request for an event item.
    /// </summary>
    [ThriftStruct( "EventItemRequest" )]
    public sealed class EventItemRequest
    {
        /// <summary>
        /// The requested item's ID.
        /// </summary>
        [ThriftField( 1, true, "eventItemId" )]
        public long ItemId { get; set; }

        /// <summary>
        /// The user's tickets that may grant them access to the item if necessary.
        /// </summary>
        [ThriftField( 3, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        /// <summary>
        /// The requested language.
        /// </summary>
        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }
    }
}