// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Camipro.Services;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.Camipro
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "camipro"; }
        }

        public bool IsVisible
        {
            get { return true; }
        }

        public bool RequiresAuthentication
        {
            get { return true; }
        }


        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<ICamiproService, CamiproService>();
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