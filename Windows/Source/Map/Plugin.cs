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
    public class Plugin : IPlugin
    {
        private const string SearchQuery = "search";
        private const string SearchQueryParameter = "q";


        public string Id
        {
            get { return "map"; }
        }

        public bool IsVisible
        {
            get { return true; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IMapService, MapService>();
            Container.Bind<IPluginSettings, PluginSettings>();

            Messenger.Register<MapSearchRequest>( navigationService.NavigateTo<MainViewModel, MapSearchRequest> );
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel, MapSearchRequest>( new MapSearchRequest() );
        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            if ( destination == SearchQuery )
            {
                navigationService.NavigateTo<MainViewModel, MapSearchRequest>( new MapSearchRequest( parameters[SearchQueryParameter] ) );
            }
            else
            {
                NavigateTo( navigationService );
            }
        }
    }
}