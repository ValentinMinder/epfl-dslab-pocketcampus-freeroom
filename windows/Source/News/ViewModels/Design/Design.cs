// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.News.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.News.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public FeedItemViewModel FeedItem { get; private set; }
        public MainViewModel Main { get; private set; }

        public Design()
        {
            var newsService = new DesignNewsService();
            var item = newsService.GetFeedsAsync( null, CancellationToken.None ).Result.Feeds[0].Items[0];

            FeedItem = new FeedItemViewModel( new DesignDataCache(), new DesignNewsService(), new DesignBrowserService(), item );
            Main = new MainViewModel( new DesignDataCache(), new DesignNewsService(), new DesignNavigationService() );

            FeedItem.OnNavigatedToAsync();
            Main.OnNavigatedToAsync();
        }
#endif
    }
}