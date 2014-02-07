// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Directory.Resources;
using PocketCampus.Directory.Services;
using PocketCampus.Directory.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Directory
{
    /// <summary>
    /// The directory Windows Phone plugin.
    /// </summary>
    public sealed class WindowsPhonePlugin : Plugin, IWindowsPhonePlugin
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
            get { return new Uri( "/Assets/DirectoryIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Gets the plugin's small icon.
        /// </summary>
        public Uri SmallIcon
        {
            get { return new Uri( "/Assets/DirectorySmallIcon.png", UriKind.Relative ); }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel>( "/PocketCampus.Directory.WindowsPhone;component/Views/MainView.xaml" );
            navigationService.Bind<PersonViewModel>( "/PocketCampus.Directory.WindowsPhone;component/Views/PersonView.xaml" );

            Container.Bind<IContactsService, ContactsService>();
        }
    }
}