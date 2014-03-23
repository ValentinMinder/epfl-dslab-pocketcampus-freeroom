// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Camipro.Resources;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Common;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Camipro
{
    /// <summary>
    /// The CAMIPRO Windows Phone plugin.
    /// </summary>
    public sealed class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
    {
        /// <summary>
        /// Gets the plugin's name.
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
            get { return new Uri( "/Assets/CamiproIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/CamiproSmallIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Camipro.WindowsPhone;component/Views/MainView.xaml" );
        }
    }
}