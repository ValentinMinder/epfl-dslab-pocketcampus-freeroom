// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Food.Resources;
using PocketCampus.Food.ViewModels;

namespace PocketCampus.Food
{
    /// <summary>
    /// The food Windows Phone plugin.
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
            get { return new Uri( "/Assets/FoodIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/FoodSmallIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Food.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<SettingsViewModel>( "/PocketCampus.Food.WindowsPhone;component/Views/SettingsView.xaml" );
        }
    }
}