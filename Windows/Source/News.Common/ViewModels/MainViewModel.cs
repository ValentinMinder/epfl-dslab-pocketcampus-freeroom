// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using ThinMvvm;
using ThinMvvm.Logging;
using PocketCampus.News.Models;
using PocketCampus.News.Services;

namespace PocketCampus.News.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/news" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
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
        public Command<FeedItem> ViewFeedItemCommand
        {
            get { return GetCommand<FeedItem>( _navigationService.NavigateTo<FeedItemViewModel, FeedItem> ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( INewsService feedsService, INavigationService navigationService )
        {
            _feedsService = feedsService;
            _navigationService = navigationService;
        }


        /// <summary>
        /// Refreshes the list of feeds.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( force )
            {
                var request = new FeedsRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    IncludeGeneralFeed = true
                };
                var response = await _feedsService.GetFeedsAsync( request );

                if ( response.Status != ResponseStatus.Success )
                {
                    throw new Exception( "A server error occurred while fetching news feeds." );
                }

                if ( !token.IsCancellationRequested )
                {
                    Feeds = response.Feeds;
                }
            }
        }
    }
}