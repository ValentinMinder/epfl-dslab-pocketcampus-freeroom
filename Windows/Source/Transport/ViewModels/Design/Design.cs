// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Transport.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Transport.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public AddStationViewModel AddStation { get; private set; }
        public MainViewModel Main { get; private set; }
        public SettingsViewModel Settings { get; private set; }

        public Design()
        {
            AddStation = new AddStationViewModel( new DesignTransportService(), new DesignLocationService(), new DesignNavigationService(), new DesignPluginSettings() );
            Main = new MainViewModel( new DesignTransportService(), new DesignPluginSettings(), new DesignNavigationService(), new DesignLocationService() );
            Settings = new SettingsViewModel( new DesignPluginSettings() );

            AddStation.OnNavigatedTo();
            Main.OnNavigatedTo();
            Settings.OnNavigatedTo();
        }
#endif
    }
}