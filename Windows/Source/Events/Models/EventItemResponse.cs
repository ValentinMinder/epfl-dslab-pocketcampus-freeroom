// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Response to a request for an event item.
    /// </summary>
    [ThriftStruct( "EventItemReply" )]
    public sealed class EventItemResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 1, true, "status" )]
        public EventsStatus Status { get; set; }

        /// <summary>
        /// The requested item.
        /// </summary>
        [ThriftField( 2, false, "eventItem" )]
        public EventItem Item { get; set; }

        /// <summary>
        /// The item's child pools, if any.
        /// </summary>
        [ThriftField( 3, false, "childrenPools" )]
        public Dictionary<long, EventPool> ChildrenPools { get; set; }

        /// <summary>
        /// The available categories, by ID.
        /// </summary>
        /// <remarks>
        /// Not just for the requested item; this contains all categories of all existing items.
        /// </remarks>
        [ThriftField( 5, false, "categs" )]
        public Dictionary<int, string> EventCategories { get; set; }

        /// <summary>
        /// The available tags, by ID.
        /// </summary>
        /// <remarks>
        /// Not just for the requested item; this contains all tags of all existing items.
        /// </remarks>
        [ThriftField( 6, false, "tags" )]
        public Dictionary<string, string> EventTags { get; set; }
    }
}