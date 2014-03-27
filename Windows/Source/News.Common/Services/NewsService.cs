// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.News.Models;
using ThriftSharp;

// Plumbing for INewsService

namespace PocketCampus.News.Services
{
    public sealed class NewsService : ThriftServiceImplementation<INewsService>, INewsService
    {
        public NewsService( IServerAccess access )
            : base( access.CreateCommunication( "news" ) )
        {
        }

        public Task<FeedsResponse> GetFeedsAsync( FeedsRequest request )
        {
            return CallAsync<FeedsRequest, FeedsResponse>( x => x.GetFeedsAsync, request );
        }

        public Task<FeedItemContentResponse> GetFeedItemContentAsync( FeedItemContentRequest request )
        {
            return CallAsync<FeedItemContentRequest, FeedItemContentResponse>( x => x.GetFeedItemContentAsync, request );
        }
    }
}