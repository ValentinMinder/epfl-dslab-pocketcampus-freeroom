// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using PocketCampus.Common;
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
    public sealed class FeedItemViewModel : CachedDataViewModel<FeedItem, FeedItemContentResponse>
    {
        private static readonly TimeSpan CacheDuration = TimeSpan.FromDays( 7 );

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
        public FeedItemViewModel( IDataCache cache, INewsService newsService, IBrowserService browserService,
                                  FeedItem item )
            : base( cache )
        {
            _newsService = newsService;
            _browserService = browserService;
            _itemId = item.Id;
        }


        protected override CachedTask<FeedItemContentResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<FeedItemContentResponse>();
            }

            var request = new FeedItemContentRequest
            {
                Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                ItemId = _itemId
            };

            return CachedTask.Create( () => _newsService.GetFeedItemContentAsync( request, token ), _itemId, DateTime.Now.Add( CacheDuration ) );
        }

        protected override bool HandleData( FeedItemContentResponse data, CancellationToken token )
        {
            if ( data.Status != ResponseStatus.Success )
            {
                throw new Exception( "A server error occurred while fetching a news item's content." );
            }

            ItemContent = data.Content;

            return true;
        }
    }
}