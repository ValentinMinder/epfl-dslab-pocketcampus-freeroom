// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for AddStationViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Transport.Services.Design;
#endif
using PocketCampus.Transport.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Transport.Views.Design
{
    public sealed class DesignAddStationViewModel : DesignViewModel<AddStationViewModel, NoParameter>
    {
#if DEBUG
        protected override AddStationViewModel ViewModel
        {
            get { return new AddStationViewModel( new DesignTransportService(), new DesignLocationService(), new DesignNavigationService(), new DesignPluginSettings() ); }
        }
#endif
    }
}