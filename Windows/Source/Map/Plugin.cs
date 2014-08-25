// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Map.Services;
using PocketCampus.Map.ViewModels;
using ThinMvvm;

namespace PocketCampus.Map
{
    /// <summary>
    /// The map plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        private const string SearchQuery = "search";
        private const string SearchQueryParameter = "q";

        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "map"; }
        }

        /// <summary>
        /// This plugin is visible in the application's main menu.
        /// </summary>
        public bool IsVisible
        {
            get { return true; }
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

        /// <summary>
        /// Navigates to the plugin from an external source, with a destination and parameters.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            if ( destination == SearchQuery )
            {
                Messenger.Send( new MapSearchRequest( parameters[SearchQueryParameter] ) );
            }
        }
    }
}