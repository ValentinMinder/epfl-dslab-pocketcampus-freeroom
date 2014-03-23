// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Map.Resources;
using PocketCampus.Map.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Map
{
    /// <summary>
    /// The map Windows Phone plugin.
    /// </summary>
    public sealed class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
    {
        /// <summary>
        /// Gets the localized name of the plugin.
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
            get { return new Uri( "/Assets/MapsIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/MapsSmallIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Map.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<SettingsViewModel>( "/PocketCampus.Map.WindowsPhone;component/Views/SettingsView.xaml" );

            Container.Bind<IPluginSettings, PluginSettings>();
        }
    }
}