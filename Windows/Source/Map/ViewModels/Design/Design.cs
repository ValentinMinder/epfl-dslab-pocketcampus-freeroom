// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Map.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Map.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }
        public SettingsViewModel Settings { get; private set; }

        public Design()
        {
            Main = new MainViewModel( new DesignLocationService(), new DesignNavigationService(), new DesignMapService(), new DesignPluginSettings(), new MapSearchRequest() );
            Settings = new SettingsViewModel( new DesignPluginSettings() );

            Main.OnNavigatedTo();
            Settings.OnNavigatedTo();
        }
#endif
    }
}