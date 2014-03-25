// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.News.Resources;
using PocketCampus.News.ViewModels;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.News
{
    /// <summary>
    /// The news Windows Phone plugin.
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
            get { return "NewsIcon"; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.News.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<FeedItemViewModel>( "/PocketCampus.News.WindowsPhone;component/Views/FeedItemView.xaml" );
        }
    }
}