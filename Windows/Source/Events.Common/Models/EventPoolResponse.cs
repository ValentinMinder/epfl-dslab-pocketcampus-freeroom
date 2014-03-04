// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Response to a request for an event pool.
    /// </summary>
    [ThriftStruct( "EventPoolReply" )]
    public sealed class EventPoolResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 1, true, "status" )]
        public EventsStatus Status { get; set; }

        /// <summary>
        /// The requested pool.
        /// </summary>
        [ThriftField( 2, false, "eventPool" )]
        public EventPool Pool { get; set; }

        /// <summary>
        /// The pool's child items, if any.
        /// </summary>
        [ThriftField( 3, false, "childrenItems" )]
        public Dictionary<long, EventItem> ChildrenItems { get; set; }

        /// <summary>
        /// The available categories, by ID.
        /// </summary>
        /// <remarks>
        /// Not just for the requested pool; this contains all categories of all existing pools.
        /// </remarks>
        [ThriftField( 5, false, "categs" )]
        public Dictionary<int, string> EventCategories { get; set; }

        /// <summary>
        /// The available tags, by ID.
        /// </summary>
        /// <remarks>
        /// Not just for the requested pool; this contains all tags of all existing pools.
        /// </remarks>
        [ThriftField( 6, false, "tags" )]
        public Dictionary<string, string> EventTags { get; set; }
    }
}