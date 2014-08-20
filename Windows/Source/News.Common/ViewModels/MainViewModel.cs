// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using PocketCampus.News.Models;
using PocketCampus.News.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.News.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/news" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, FeedsResponse>
    {
        private readonly INewsService _feedsService;
        private readonly INavigationService _navigationService;

        private Feed[] _feeds;

        /// <summary>
        /// Gets the list of feeds.
        /// </summary>
        public Feed[] Feeds
        {
            get { return _feeds; }
            private set { SetProperty( ref _feeds, value ); }
        }

        /// <summary>
        /// Gets the command executed to view a feed item.
        /// </summary>
        [LogId( "OpenNewsItem" )]
        [LogParameter( "$Param.LogId" )]
        public Command<FeedItem> ViewFeedItemCommand
        {
            get { return this.GetCommand<FeedItem>( _navigationService.NavigateTo<FeedItemViewModel, FeedItem> ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( IDataCache cache, INewsService feedsService, INavigationService navigationService )
            : base( cache )
        {
            _feedsService = feedsService;
            _navigationService = navigationService;
        }


        protected override CachedTask<FeedsResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<FeedsResponse>();
            }

            var request = new FeedsRequest
            {
                Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                IncludeGeneralFeed = true
            };

            return CachedTask.Create( () => _feedsService.GetFeedsAsync( request, token ) );
        }

        protected override bool HandleData( FeedsResponse data, CancellationToken token )
        {
            if ( data.Status != ResponseStatus.Success )
            {
                throw new Exception( "A server error occurred while fetching news feeds." );
            }

            if ( !token.IsCancellationRequested )
            {
                Feeds = data.Feeds;
            }

            return true;
        }
    }
}