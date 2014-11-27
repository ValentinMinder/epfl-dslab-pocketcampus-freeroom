// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Transport.Resources;
using PocketCampus.Transport.Services;
using PocketCampus.Transport.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Transport
{
    /// <summary>
    /// The transport Windows Phone plugin.
    /// </summary>
    public class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
    {
        /// <summary>
        /// Gets the plugin's localized name.
        /// </summary>
        public string Name
        {
            get { return PluginResources.PluginName; }
        }

        /// <summary>
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        public string IconKey
        {
            get { return "TransportIcon"; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Transport.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<AddStationViewModel>( "/PocketCampus.Transport.WindowsPhone;component/Views/AddStationView.xaml" );
            navigationService.Bind<SettingsViewModel>( "/PocketCampus.Transport.WindowsPhone;component/Views/SettingsView.xaml" );

            Container.Bind<IPluginSettings, PluginSettings>();
        }
    }
}