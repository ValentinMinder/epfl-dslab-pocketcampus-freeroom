// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Transport.Services;
using PocketCampus.Transport.ViewModels;
using ThinMvvm;

namespace PocketCampus.Transport
{
    /// <summary>
    /// The transport plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "transport"; }
        }

        /// <summary>
        /// This plugin is visible in the application's main menu.
        /// </summary>
        public bool IsVisible
        {
            get { return true; }
        }

        /// <summary>
        /// The plugin does not require authentication.
        /// </summary>
        public bool RequiresAuthentication
        {
            get { return false; }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<ITransportService, TransportService>();
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel>();
        }

        /// <summary>
        /// This plugin does not handle external navigation.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
    }
}