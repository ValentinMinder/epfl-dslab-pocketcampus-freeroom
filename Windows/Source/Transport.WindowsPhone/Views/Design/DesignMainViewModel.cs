// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Transport.Services.Design;
#endif
using PocketCampus.Transport.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Transport.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignTransportService(), new DesignPluginSettings(), new DesignNavigationService(), new DesignLocationService() ); }
        }
#endif
    }
}