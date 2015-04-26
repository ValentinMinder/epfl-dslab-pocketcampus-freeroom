// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    [ThriftStruct( "NewsFeedItemContentRequest" )]
    public sealed class FeedItemContentRequest
    {
        [ThriftField( 1, true, "language" )]
        public string Language { get; set; }

        [ThriftField( 2, true, "itemId" )]
        public int ItemId { get; set; }
    }
}