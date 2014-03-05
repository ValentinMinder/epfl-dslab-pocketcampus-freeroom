// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Response to a request for an e-mail with favorite items.
    /// </summary>
    [ThriftStruct( "SendEmailReply" )]
    public sealed class FavoriteEmailResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 1, true, "status" )]
        public EventsStatus Status { get; set; }
    }
}