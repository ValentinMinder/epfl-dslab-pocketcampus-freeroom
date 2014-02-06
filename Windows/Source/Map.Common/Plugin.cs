// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Map.Services;
using PocketCampus.Map.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Map
{
    /// <summary>
    /// The map plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "Map"; }
        }

        /// <summary>
        /// This plugin does not require authentication.
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
            Container.Bind<IMapService, MapService>();

            Messenger.Register<MapSearchRequest>( navigationService.NavigateTo<MainViewModel, MapSearchRequest> );
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel, MapSearchRequest>( new MapSearchRequest() );
        }
    }
}