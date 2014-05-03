// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Net;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main
{
    public static class AppInitializer
    {
        public static void BindImplementations()
        {
            DataViewModelOptions.AddNetworkExceptionType( typeof( WebException ) );
            DataViewModelOptions.AddNetworkExceptionType( typeof( OperationCanceledException ) );

            Logger.WarnOnMissingAttributes = true;

            Container.Bind<IMainSettings, MainSettings>();
            Container.Bind<IServerAccess, ServerAccess>();
            Container.Bind<IAuthenticationService, AuthenticationService>();
            Container.Bind<ITequilaAuthenticator, TequilaAuthenticator>();
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();
        }

        public static void InitializePlugins( IPluginLoader pluginLoader, INavigationService navigationService )
        {
            foreach ( var plugin in pluginLoader.GetPlugins() )
            {
                plugin.Initialize( navigationService );
            }
        }
    }
}