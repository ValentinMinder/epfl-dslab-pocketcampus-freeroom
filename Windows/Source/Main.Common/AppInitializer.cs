// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Mvvm;
using ThriftSharp;

namespace PocketCampus.Main
{
    public static class AppInitializer
    {
        public static void BindImplementations()
        {
            DataViewModelOptions.NetworkExceptionType = typeof( ThriftTransportException );

            Container.Bind<ITequilaAuthenticator, TequilaAuthenticator>();
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();
            Container.Bind<IAuthenticationService, AuthenticationService>();
            Container.BindOnce<IServerAccess, ServerAccess>();
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