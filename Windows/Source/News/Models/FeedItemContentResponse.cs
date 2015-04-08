// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    [ThriftStruct( "NewsFeedItemContentResponse" )]
    public sealed class FeedItemContentResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        [ThriftField( 2, false, "content" )]
        public FeedItemContent Content { get; set; }
    }
}