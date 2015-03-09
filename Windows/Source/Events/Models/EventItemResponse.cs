// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventItemReply" )]
    public sealed class EventItemResponse
    {
        [ThriftField( 1, true, "status" )]
        public EventsStatus Status { get; set; }

        [ThriftField( 2, false, "eventItem" )]
        public EventItem Item { get; set; }

        [ThriftField( 3, false, "childrenPools" )]
        public Dictionary<long, EventPool> ChildrenPools { get; set; }

        // Not just for the requested item; this contains all categories of all existing items.
        [ThriftField( 5, false, "categs" )]
        public Dictionary<int, string> EventCategories { get; set; }

        // Not just for the requested item; this contains all tags of all existing items.
        [ThriftField( 6, false, "tags" )]
        public Dictionary<string, string> EventTags { get; set; }
    }
}