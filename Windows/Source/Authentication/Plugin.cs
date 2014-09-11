// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Authentication.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;

namespace PocketCampus.Authentication
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "Authentication"; }
        }

        public bool IsVisible
        {
            get { return false; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IAuthenticationService, AuthenticationService>();
            Container.Bind<IAuthenticator, TequilaAuthenticator>();
        }

        // This plugin cannot be navigated to.
        public void NavigateTo( INavigationService navigationService ) { }

        // This plugin does not handle navigation from external sources.
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
    }
}