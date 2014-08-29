// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for FeedItemViewModel

#if DEBUG
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.News.Services.Design;
#endif
using PocketCampus.News.Models;
using PocketCampus.News.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.News.Views.Design
{
    public sealed class DesignFeedItemViewModel : DesignViewModel<FeedItemViewModel, FeedItem>
    {
#if DEBUG
        protected override FeedItemViewModel ViewModel
        {
            get
            {
                var newsService = new DesignNewsService();
                var item = newsService.GetFeedsAsync( null, CancellationToken.None ).Result.Feeds[0].Items[0];
                return new FeedItemViewModel( new DesignDataCache(), newsService, new DesignBrowserService(), item );
            }
        }
#endif
    }
}