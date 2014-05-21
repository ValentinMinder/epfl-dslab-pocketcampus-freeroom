// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.News.Models;
using ThriftSharp;

namespace PocketCampus.News.Services
{
    /// <summary>
    /// The news server service.
    /// </summary>
    [ThriftService( "NewsService" )]
    public interface INewsService
    {
        /// <summary>
        /// Asynchronously gets all available feeds.
        /// </summary>
        [ThriftMethod( "getAllFeeds" )]
        Task<FeedsResponse> GetFeedsAsync( [ThriftParameter( 1, "request" )] FeedsRequest request, CancellationToken cancellationToken );

        /// <summary>
        /// Asynchronously gets the content of a feed item.
        /// </summary>
        [ThriftMethod( "getFeedItemContent" )]
        Task<FeedItemContentResponse> GetFeedItemContentAsync( [ThriftParameter( 1, "request" )] FeedItemContentRequest request, CancellationToken cancellationToken );
    }
}