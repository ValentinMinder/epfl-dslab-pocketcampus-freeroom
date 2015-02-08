// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.News.Models;
using ThriftSharp;

namespace PocketCampus.News.Services
{
    [ThriftService( "NewsService" )]
    public interface INewsService
    {
        [ThriftMethod( "getAllFeeds" )]
        Task<FeedsResponse> GetFeedsAsync( [ThriftParameter( 1, "request" )] FeedsRequest request, CancellationToken cancellationToken );

        [ThriftMethod( "getFeedItemContent" )]
        Task<FeedItemContentResponse> GetFeedItemContentAsync( [ThriftParameter( 1, "request" )] FeedItemContentRequest request, CancellationToken cancellationToken );
    }
}