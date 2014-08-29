// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Events.Resources;
using PocketCampus.Events.Services;
using PocketCampus.Events.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Events
{
    /// <summary>
    /// The Events Windows Phone plugin.
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
        /// Gets the key of the plugin's icon in the application resources.
        /// </summary>
        public string IconKey
        {
            get { return "EventsIcon"; }
        }

        /// <summary>
        /// Initializes the Windows Phone specific part of the plugin.
        /// </summary>
        public void Initialize( IWindowsPhoneNavigationService navigationService )
        {
            navigationService.Bind<EventPoolViewModel>( "/PocketCampus.Events.WindowsPhone;component/Views/EventPoolView.xaml" );
            navigationService.Bind<EventItemViewModel>( "/PocketCampus.Events.WindowsPhone;component/Views/EventItemView.xaml" );
            navigationService.Bind<SettingsViewModel>( "/PocketCampus.Events.WindowsPhone;component/Views/SettingsView.xaml" );
            navigationService.Bind<CategoryFilterViewModel>( "/PocketCampus.Events.WindowsPhone;component/Views/CategoryFilterView.xaml" );
            navigationService.Bind<TagFilterViewModel>( "/PocketCampus.Events.WindowsPhone;component/Views/TagFilterView.xaml" );

            Container.Bind<IEmailPrompt, EmailPrompt>();
            Container.Bind<ICodeScanner, CodeScanner>();
        }
    }
}