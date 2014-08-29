// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for EventItemViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Events.Services.Design;
#endif
using PocketCampus.Events.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Events.Views.Design
{
    public sealed class DesignEventItemViewModel : DesignViewModel<EventItemViewModel, ViewEventItemRequest>
    {
#if DEBUG
        protected override EventItemViewModel ViewModel
        {
            get
            {
                var request = new ViewEventItemRequest( 0, true );
                return new EventItemViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignBrowserService(), new DesignEventsService(), new DesignPluginSettings(), request );
            }
        }
#endif
    }
}