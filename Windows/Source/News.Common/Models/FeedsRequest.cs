// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Request for feeds.
    /// </summary>
    [ThriftStruct( "NewsFeedRequest" )]
    public sealed class FeedsRequest
    {
        /// <summary>
        /// The language the feeds should be in.
        /// </summary>
        [ThriftField( 1, true, "language" )]
        public string Language { get; set; }

        /// <summary>
        /// Whether to include the "all news" feed.
        /// </summary>
        [ThriftField( 2, true, "generalFeedIncluded" )]
        public bool IncludeGeneralFeed { get; set; }
    }
}