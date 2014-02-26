﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventPoolReply" )]
    public sealed class EventPoolResponse
    {
        [ThriftField( 1, true, "status" )]
        public EventsStatusCode Status { get; set; }

        [ThriftField( 2, false, "eventPool" )]
        public EventPool Pool { get; set; }

        [ThriftField( 3, false, "childrenItems" )]
        public Dictionary<long, EventItem> ChildrenItems { get; set; }

        [ThriftField( 5, false, "categs" )]
        public Dictionary<int, string> EventCategories { get; set; }

        [ThriftField( 6, false, "tags" )]
        public Dictionary<string, string> EventTags { get; set; }
    }
}