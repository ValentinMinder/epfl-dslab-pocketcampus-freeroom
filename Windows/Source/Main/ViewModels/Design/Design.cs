﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Authentication.Services.Design;
using PocketCampus.Common.Services.Design;
using PocketCampus.Main.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Main.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public AboutViewModel About { get; private set; }
        public MainViewModel Main { get; private set; }
        public SettingsViewModel Settings { get; private set; }

        public Design()
        {
            About = new AboutViewModel( new DesignBrowserService(), new DesignEmailService(), new DesignRatingService() );
            Main = new MainViewModel( new DesignNavigationService(), new DesignServerAccess(), new DesignPluginLoader(),
                                      new DesignMainSettings(), new DesignTileService() );
            Settings = new SettingsViewModel( new DesignMainSettings(), new DesignAuthenticator(), new DesignNavigationService(),
                                              new DesignAuthenticationService(), new DesignCredentialsStorage(), new DesignTileService() );

            About.OnNavigatedTo();
            Main.OnNavigatedToAsync();
            Settings.OnNavigatedTo();
        }
#endif
    }
}