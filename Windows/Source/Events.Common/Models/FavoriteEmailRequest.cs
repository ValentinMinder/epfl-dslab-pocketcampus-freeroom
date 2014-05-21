// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Events.Models
{
    /// <summary>
    /// Request for an e-mail with the items marked as favorite by the user.
    /// </summary>
    [ThriftStruct( "SendEmailRequest" )]
    public sealed class FavoriteEmailRequest
    {
        /// <summary>
        /// The ID of the pool the items are in.
        /// </summary>
        [ThriftField( 4, true, "eventPoolId" )]
        public long PoolId { get; set; }

        /// <summary>
        /// The items marked as favorite by the user.
        /// </summary>
        [ThriftField( 1, true, "starredEventItems" )]
        public long[] FavoriteItems { get; set; }

        /// <summary>
        /// The user's tickets that may grant them access to the pool and items if necessary.
        /// </summary>
        [ThriftField( 2, false, "userTickets" )]
        public string[] UserTickets { get; set; }

        /// <summary>
        /// The address the e-mail will be sent to.
        /// </summary>
        [ThriftField( 3, false, "emailAddress" )]
        public string EmailAddress { get; set; }

        /// <summary>
        /// The language the e-mail should be in.
        /// </summary>
        [ThriftField( 5, false, "lang" )]
        public string Language { get; set; }
    }
}