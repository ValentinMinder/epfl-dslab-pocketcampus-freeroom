// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using PocketCampus.Common.Services;
using PocketCampus.News.Models;
using PocketCampus.News.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.News.ViewModels
{
    [LogId( "/news/item" )]
    public sealed class FeedItemViewModel : CachedDataViewModel<FeedItem, FeedItemContentResponse>
    {
        private static readonly TimeSpan CacheDuration = TimeSpan.FromDays( 7 );

        private readonly INewsService _newsService;
        private readonly IBrowserService _browserService;

        private FeedItemContent _itemContent;


        public FeedItem Item { get; private set; }

        public FeedItemContent ItemContent
        {
            get { return _itemContent; }
            private set { SetProperty( ref _itemContent, value ); }
        }


        [LogId( "ViewInBrowser" )]
        [LogParameter( "Item.LogId" )]
        public Command OpenInBrowserCommand
        {
            get { return this.GetCommand( () => _browserService.NavigateTo( ItemContent.Url ) ); }
        }


        public FeedItemViewModel( IDataCache cache, INewsService newsService, IBrowserService browserService,
                                  FeedItem item )
            : base( cache )
        {
            _newsService = newsService;
            _browserService = browserService;
            Item = item;
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
                ItemId = Item.Id
            };

            return CachedTask.Create( () => _newsService.GetFeedItemContentAsync( request, token ), Item.Id, DateTime.Now.Add( CacheDuration ) );
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