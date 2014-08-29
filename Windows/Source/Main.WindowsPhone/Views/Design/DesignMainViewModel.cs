// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Main.Services.Design;
#endif
using PocketCampus.Main.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Main.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, ViewPluginRequest>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignNavigationService(), new DesignServerAccess(), new DesignPluginLoader(), new DesignMainSettings(), new DesignTileService(), new ViewPluginRequest() ); }
        }
#endif
    }
}