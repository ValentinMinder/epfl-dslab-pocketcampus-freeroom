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
        private const string FeedNamePrefix = "EPFL ";

        public NewsService( IServerConfiguration config )
            : base( config.CreateCommunication( "news" ) )
        {
        }

        public async Task<Feed[]> GetFeedsAsync( string language )
        {
            var feeds = await CallAsync<string, Feed[]>( x => x.GetFeedsAsync, language );
            foreach ( var feed in feeds )
            {
                feed.Name = feed.Name.Replace( FeedNamePrefix, "" );
            }
            return feeds;
        }

        public Task<string> GetFeedItemContentAsync( long id )
        {
            return CallAsync<long, string>( x => x.GetFeedItemContentAsync, id );
        }
    }
}