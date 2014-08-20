// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Net;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;

namespace PocketCampus.Main
{
    public static class AppInitializer
    {
        public static void Initialize( IPluginLoader pluginLoader, INavigationService navigationService )
        {
            DataViewModelOptions.AddNetworkExceptionType( typeof( WebException ) );
            DataViewModelOptions.AddNetworkExceptionType( typeof( OperationCanceledException ) );

            Container.Bind<IMainSettings, MainSettings>();
            Container.Bind<IServerAccess, ServerAccess>();

            foreach ( var plugin in pluginLoader.GetPlugins() )
            {
                plugin.Initialize( navigationService );
            }

            // SecureRequestHandler depends on the auth plugin, so it must be initialized after it
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();
        }
    }
}