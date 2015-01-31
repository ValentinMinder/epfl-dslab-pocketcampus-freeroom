// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Satellite.Services;
using PocketCampus.Satellite.ViewModels;
using ThinMvvm;

namespace PocketCampus.Satellite
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "satellite"; }
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
            Container.Bind<ISatelliteService, SatelliteService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel>();
        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            NavigateTo( navigationService );
        }
    }
}