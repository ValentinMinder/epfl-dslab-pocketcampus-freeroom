// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using PocketCampus.Transport.Resources;
using PocketCampus.Transport.ViewModels;

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
        /// Gets the plugin's icon.
        /// </summary>
        public Uri Icon
        {
            get { return new Uri( "/Assets/TransportIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/TransportSmallIcon.png", UriKind.Relative ); }
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