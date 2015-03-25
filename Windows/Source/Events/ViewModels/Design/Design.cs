// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using System.Linq;
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Events.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Events.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public CategoryFilterViewModel CategoryFilter { get; private set; }
        public EventItemViewModel EventItem { get; private set; }
        public EventPoolViewModel EventPool { get; private set; }
        public SettingsViewModel Settings { get; private set; }
        public TagFilterViewModel TagFilter { get; private set; }

        public Design()
        {
            var eventsService = new DesignEventsService();
            var result = eventsService.GetEventPoolAsync( null, CancellationToken.None ).Result;
            var pool = result.Pool;
            pool.Items = result.ChildrenItems.Values.ToArray();
            var itemRequest = new ViewEventItemRequest( 0, true );

            CategoryFilter = new CategoryFilterViewModel( new DesignPluginSettings(), pool );
            EventItem = new EventItemViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignBrowserService(),
                                                new DesignEventsService(), new DesignPluginSettings(),
                                                itemRequest );
            EventPool = new EventPoolViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignEventsService(),
                                                new DesignPluginSettings(), new DesignEmailPrompt(), new DesignCodeScanner(),
                                                Models.EventPool.RootId );
            Settings = new SettingsViewModel( new DesignPluginSettings() );
            TagFilter = new TagFilterViewModel( new DesignPluginSettings(), pool );

            CategoryFilter.OnNavigatedTo();
            EventItem.OnNavigatedToAsync();
            EventPool.OnNavigatedToAsync();
            Settings.OnNavigatedTo();
            TagFilter.OnNavigatedTo();
        }
#endif
    }
}