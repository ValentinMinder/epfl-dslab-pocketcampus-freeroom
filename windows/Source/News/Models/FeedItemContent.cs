// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    [ThriftStruct( "NewsFeedItemContent" )]
    public sealed class FeedItemContent
    {
        [ThriftField( 1, true, "feedName" )]
        public string FeedName { get; set; }

        [ThriftField( 2, true, "title" )]
        public string Title { get; set; }

        [ThriftField( 3, true, "link" )]
        public string Url { get; set; }

        [ThriftField( 4, true, "content" )]
        public string Content { get; set; }

        // Contains {x} and {y} tokens to change its size.
        [ThriftField( 5, false, "imageUrl" )]
        public string ImageUrl { get; set; }
    }
}