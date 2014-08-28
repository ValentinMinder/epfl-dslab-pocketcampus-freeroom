// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for SettingsViewModel

using PocketCampus.Authentication.Services.Design;
using PocketCampus.Common.Services.Design;
using PocketCampus.Main.Services.Design;
using PocketCampus.Main.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Main.Views.Design
{
    public sealed class DesignSettingsViewModel : DesignViewModel<SettingsViewModel, NoParameter>
    {
#if DEBUG
        protected override SettingsViewModel ViewModel
        {
            get { return new SettingsViewModel( new DesignMainSettings(), new DesignTequilaAuthenticator(), new DesignNavigationService(), new DesignAuthenticationService(), new DesignCredentialsStorage(), new DesignTileService() ); }
        }
#endif
    }
}