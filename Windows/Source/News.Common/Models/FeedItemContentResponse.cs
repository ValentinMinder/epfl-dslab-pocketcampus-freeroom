// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Response for a feed item content request.
    /// </summary>
    [ThriftStruct( "NewsFeedItemContentResponse" )]
    public sealed class FeedItemContentResponse
    {
        /// <summary>
        /// The response status.
        /// </summary>
        [ThriftField( 1, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        /// <summary>
        /// The requested item content.
        /// </summary>
        [ThriftField( 2, false, "content" )]
        public FeedItemContent Content { get; set; }
    }
}