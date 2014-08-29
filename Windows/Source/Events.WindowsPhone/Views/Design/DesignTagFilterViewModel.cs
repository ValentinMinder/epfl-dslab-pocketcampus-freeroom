// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for TagFilterViewModel

#if DEBUG
using System.Threading;
using PocketCampus.Events.Services.Design;
#endif
using PocketCampus.Events.Models;
using PocketCampus.Events.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Events.Views.Design
{
    public sealed class DesignTagFilterViewModel : DesignViewModel<TagFilterViewModel, EventPool>
    {
#if DEBUG
        protected override TagFilterViewModel ViewModel
        {
            get
            {
                var pool = new DesignEventsService().GetEventPoolAsync( null, CancellationToken.None ).Result.Pool;
                return new TagFilterViewModel( new DesignPluginSettings(), pool );
            }
        }
#endif
    }
}