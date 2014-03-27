// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThriftSharp;

namespace PocketCampus.Main
{
    public static class AppInitializer
    {
        public static void BindImplementations()
        {
            DataViewModelOptions.NetworkExceptionType = typeof( ThriftTransportException );

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