// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.News.Models;
using ThriftSharp;

// Plumbing for INewsService

namespace PocketCampus.News.Services
{
    public sealed class NewsService : ThriftServiceImplementation<INewsService>, INewsService
    {
        public NewsService( IServerAccess access ) : base( access.CreateCommunication( "news" ) ) { }

        public Task<FeedsResponse> GetFeedsAsync( FeedsRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<FeedsRequest, CancellationToken, FeedsResponse>( x => x.GetFeedsAsync, request, cancellationToken );
        }

        public Task<FeedItemContentResponse> GetFeedItemContentAsync( FeedItemContentRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<FeedItemContentRequest, CancellationToken, FeedItemContentResponse>( x => x.GetFeedItemContentAsync, request, cancellationToken );
        }
    }
}