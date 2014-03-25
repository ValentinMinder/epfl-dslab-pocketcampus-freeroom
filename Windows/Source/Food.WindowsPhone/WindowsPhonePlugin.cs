// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Food.Resources;
using PocketCampus.Food.ViewModels;
using ThinMvvm.WindowsPhone;

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
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        public string IconKey
        {
            get { return "FoodIcon"; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Food.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<RatingViewModel>( "/PocketCampus.Food.WindowsPhone;component/Views/RatingView.xaml" );
            navigationService.Bind<SettingsViewModel>( "/PocketCampus.Food.WindowsPhone;component/Views/SettingsView.xaml" );
        }
    }
}