// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    [ThriftStruct( "NewsFeed" )]
    public sealed class Feed
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        [ThriftField( 2, true, "items" )]
        public FeedItem[] Items { get; set; }
    }
}