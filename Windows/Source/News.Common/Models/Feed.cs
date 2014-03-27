// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// EPFL news feed.
    /// </summary>
    [ThriftStruct( "NewsFeed" )]
    public sealed class Feed
    {
        /// <summary>
        /// The feed's name.
        /// </summary>
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        /// <summary>
        /// The feed's items.
        /// </summary>
        [ThriftField( 2, true, "items" )]
        public FeedItem[] Items { get; set; }
    }
}