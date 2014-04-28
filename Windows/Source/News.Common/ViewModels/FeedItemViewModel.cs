// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.News.Models;
using PocketCampus.News.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.News.ViewModels
{
    /// <summary>
    /// The ViewModel for feed items.
    /// </summary>
    [LogId( "/news/item" )]
    public sealed class FeedItemViewModel : DataViewModel<FeedItem>
    {
        private readonly INewsService _newsService;
        private readonly IBrowserService _browserService;
        private readonly int _itemId;

        private FeedItemContent _itemContent;

        /// <summary>
        /// Gets the feed item's content.
        /// </summary>
        public FeedItemContent ItemContent
        {
            get { return _itemContent; }
            private set { SetProperty( ref _itemContent, value ); }
        }

        /// <summary>
        /// Gets the command executed to open the feed item in the browser.
        /// </summary>
        [LogId( "ViewInBrowser" )]
        public Command OpenInBrowserCommand
        {
            get { return GetCommand( () => _browserService.NavigateTo( ItemContent.Url ) ); }
        }


        /// <summary>
        /// Creates a new FeedItemViewModel.
        /// </summary>
        public FeedItemViewModel( INewsService newsService, IBrowserService browserService,
                                  FeedItem item )
        {
            _newsService = newsService;
            _browserService = browserService;
            _itemId = item.Id;
        }

        /// <summary>
        /// Refreshes the data.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            var request = new FeedItemContentRequest
            {
                Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                ItemId = _itemId
            };

            var response = await _newsService.GetFeedItemContentAsync( request, token );

            if ( response.Status != ResponseStatus.Success )
            {
                throw new Exception( "A server error occurred while fetching a news item's content." );
            }

            ItemContent = response.Content;
        }
    }
}