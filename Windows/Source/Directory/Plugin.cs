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
    public class Plugin : IPlugin
    {
        private const string SearchQuery = "search";
        private const string SearchQueryParameter = "q";
        private const string ViewPersonQuery = "view";


        public string Id
        {
            get { return "directory"; }
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
            Container.Bind<IDirectoryService, DirectoryService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel, ViewPersonRequest>( new ViewPersonRequest() );
        }

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

                default:
                    NavigateTo( navigationService );
                    break;
            }
        }
    }
}