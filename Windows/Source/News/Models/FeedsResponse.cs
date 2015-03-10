// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// Response for a feeds request.
    /// </summary>
    [ThriftStruct( "NewsFeedsResponse" )]
    public sealed class FeedsResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        [ThriftField( 2, true, "feeds" )]
        public Feed[] Feeds { get; set; }
    }
}