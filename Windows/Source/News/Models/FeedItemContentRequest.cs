// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Request for a feed item's content.
    /// </summary>
    [ThriftStruct( "NewsFeedItemContentRequest" )]
    public sealed class FeedItemContentRequest
    {
        /// <summary>
        /// The language the content should be in.
        /// </summary>
        [ThriftField( 1, true, "language" )]
        public string Language { get; set; }

        /// <summary>
        /// The ID of the item whose content is requested.
        /// </summary>
        [ThriftField( 2, true, "itemId" )]
        public int ItemId { get; set; }
    }
}