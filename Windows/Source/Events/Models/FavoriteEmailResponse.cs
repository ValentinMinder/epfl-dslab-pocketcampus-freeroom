// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "SendEmailReply" )]
    public sealed class FavoriteEmailResponse
    {
        [ThriftField( 1, true, "status" )]
        public EventsStatus Status { get; set; }
    }
}