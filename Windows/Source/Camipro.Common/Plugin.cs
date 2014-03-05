// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Camipro.Services;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Common;
using PocketCampus.Mvvm;

namespace PocketCampus.Camipro
{
    /// <summary>
    /// The CAMIPRO plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "Camipro"; }
        }

        /// <summary>
        /// This plugin requires authentication.
        /// </summary>
        public bool RequiresAuthentication
        {
            get { return true; }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<ICamiproService, CamiproService>();
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