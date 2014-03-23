// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Moodle.Resources;
using PocketCampus.Moodle.Services;
using PocketCampus.Moodle.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Moodle
{
    /// <summary>
    /// The Moodle Windows Phone plugin.
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
            get { return new Uri( "/Assets/MoodleIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/MoodleSmallIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Moodle.WindowsPhone;component/Views/MainView.xaml" );

            Container.Bind<IMoodleDownloader, MoodleDownloader>();
            Container.Bind<IFileStorage, FileStorage>();
        }
    }
}