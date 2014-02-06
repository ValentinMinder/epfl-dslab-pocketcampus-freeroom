// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;
using PocketCampus.News.Models;
using PocketCampus.News.Services;

namespace PocketCampus.News.ViewModels
{
    /// <summary>
    /// The ViewModel for feed items.
    /// </summary>
    [PageLogId( "/news/item" )]
    public sealed class FeedItemViewModel : DataViewModel<FeedItem>
    {
        private readonly INewsService _newsService;
        private readonly IBrowserService _browserService;

        private string _itemContent;

        /// <summary>
        /// Gets the feed item that is being displayed.
        /// </summary>
        public FeedItem Item { get; private set; }

        /// <summary>
        /// Gets the feed item's content as HTML.
        /// </summary>
        public string ItemContent
        {
            get { return _itemContent; }
            private set { SetProperty( ref _itemContent, value ); }
        }

        /// <summary>
        /// Gets the command executed to open the feed item in the browser.
        /// </summary>
        [CommandLogId( "ViewInBrowser" )]
        public Command OpenInBrowserCommand
        {
            get { return GetCommand( () => _browserService.NavigateTo( Item.Url ) ); }
        }


        /// <summary>
        /// Creates a new FeedItemViewModel.
        /// </summary>
        public FeedItemViewModel( INewsService newsService, IBrowserService browserService,
                                  FeedItem item )
        {
            _newsService = newsService;
            _browserService = browserService;
            Item = item;
        }


        /// <summary>
        /// Called when the user navigates to the ViewModel.
        /// </summary>
        public override Task OnNavigatedToAsync()
        {
            return TryExecuteAsync( async _ => ItemContent = await _newsService.GetFeedItemContentAsync( Item.Id ) );
        }
    }
}