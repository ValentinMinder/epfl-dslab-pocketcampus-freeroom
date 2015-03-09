// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    [ThriftStruct( "SendEmailRequest" )]
    public sealed class FavoriteEmailRequest
    {
        [ThriftField( 4, true, "eventPoolId" )]
        public long PoolId { get; set; }

        [ThriftField( 1, true, "starredEventItems" )]
        public long[] FavoriteItems { get; set; }

        [ThriftField( 2, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        [ThriftField( 3, false, "emailAddress" )]
        public string EmailAddress { get; set; }

        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }
    }
}