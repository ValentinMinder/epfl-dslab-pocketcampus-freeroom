// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Directory.Resources;
using PocketCampus.Directory.Services;
using PocketCampus.Directory.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone;

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
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        public string IconKey
        {
            get { return "DirectoryIcon"; }
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