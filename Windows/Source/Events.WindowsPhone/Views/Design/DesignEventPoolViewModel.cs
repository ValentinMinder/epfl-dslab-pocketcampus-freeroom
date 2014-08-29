// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for EventPoolViewModel

using PocketCampus.Events.Services.Design;
using PocketCampus.Events.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Events.Views.Design
{
    public sealed class DesignEventPoolViewModel : DesignViewModel<EventPoolViewModel, long>
    {
#if DEBUG
        protected override EventPoolViewModel ViewModel
        {
            get { return new EventPoolViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignEventsService(), new DesignPluginSettings(), new DesignEmailPrompt(), new DesignCodeScanner(), 0 ); }
        }
#endif
    }
}