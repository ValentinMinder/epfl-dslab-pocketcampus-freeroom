// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services;
using PocketCampus.Directory.ViewModels;
using ThinMvvm;

namespace PocketCampus.Directory
{
    /// <summary>
    /// The directory plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        private const string SearchQuery = "search";
        private const string SearchQueryParameter = "q";
        private const string ViewPersonQuery = "view";

        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "directory"; }
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
            Container.Bind<IDirectoryService, DirectoryService>();
        }

        /// <summary>
        /// Navigates to the plugin's main ViewModel.
        /// </summary>
        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel, ViewPersonRequest>( new ViewPersonRequest() );
        }

        /// <summary>
        /// Navigates to the plugin from an external source, with a destination and parameters.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            switch ( destination )
            {
                case SearchQuery:
                    navigationService.NavigateTo<MainViewModel, ViewPersonRequest>( new ViewPersonRequest( parameters[SearchQueryParameter] ) );
                    break;

                case ViewPersonQuery:
                    navigationService.NavigateTo<PersonViewModel, Person>( Person.Parse( parameters ) );
                    break;
            }
        }
    }
}