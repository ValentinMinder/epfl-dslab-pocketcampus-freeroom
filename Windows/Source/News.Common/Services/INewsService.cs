// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

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
        /// Asynchronously gets all EPFL news feeds.
        /// </summary>
        [ThriftMethod( "getFeeds" )]
        Task<Feed[]> GetFeedsAsync( [ThriftParameter( 1, "language" )] string language );

        /// <summary>
        /// Asynchronously gets the content of a feed item as HTML.
        /// </summary>
        [ThriftMethod( "getNewsItemContent" )]
        Task<string> GetFeedItemContentAsync( [ThriftParameter( 1, "newsItemId" )] long id );
    }
}