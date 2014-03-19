// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Content of a feed item.
    /// </summary>
    [ThriftStruct( "NewsFeedItemContent" )]
    public sealed class FeedItemContent
    {
        /// <summary>
        /// The name of the feed the item belongs to.
        /// </summary>
        [ThriftField( 1, true, "feedName" )]
        public string FeedName { get; set; }

        /// <summary>
        /// The item's title.
        /// </summary>
        [ThriftField( 2, true, "title" )]
        public string Title { get; set; }

        /// <summary>
        /// URL to view the news item in a browser.
        /// </summary>
        [ThriftField( 3, true, "link" )]
        public string Url { get; set; }

        /// <summary>
        /// The item's content, as HTML.
        /// </summary>
        [ThriftField( 4, true, "content" )]
        public string Content { get; set; }

        /// <summary>
        /// URL to the item's picture, if there is any.
        /// </summary>
        /// <remarks>
        /// Contains {x} and {y} tokens to change its size.
        /// </remarks>
        [ThriftField( 5, false, "imageUrl" )]
        public string ImageUrl { get; set; }
    }
}