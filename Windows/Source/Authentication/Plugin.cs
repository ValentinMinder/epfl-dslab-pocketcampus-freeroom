// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Authentication.Services;
using PocketCampus.Authentication.ViewModels;
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
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();

            Messenger.Register<AuthenticationRequest>( navigationService.NavigateTo<MainViewModel, AuthenticationRequest> );
        }

        // This plugin cannot be navigated to.
        public void NavigateTo( INavigationService navigationService )
        {
            throw new NotSupportedException();
        }

        // This plugin does not handle navigation from external sources.
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            throw new NotSupportedException();
        }
    }
}