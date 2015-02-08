// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "EventItemRequest" )]
    public sealed class EventItemRequest
    {
        [ThriftField( 1, true, "eventItemId" )]
        public long ItemId { get; set; }

        [ThriftField( 3, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }
    }
}