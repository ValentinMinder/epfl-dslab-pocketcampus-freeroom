// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Net;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;

namespace PocketCampus.Main
{
    public static class AppInitializer
    {
        public static async Task InitializeAsync( IPluginLoader pluginLoader, INavigationService navigationService )
        {
            DataViewModelOptions.AddNetworkExceptionType( typeof( WebException ) );
            DataViewModelOptions.AddNetworkExceptionType( typeof( OperationCanceledException ) );

            var settings = Container.Bind<IMainSettings, MainSettings>();
            var serverAccess = Container.Bind<IServerAccess, ServerAccess>();

            foreach ( var plugin in await pluginLoader.GetPluginsAsync() )
            {
                plugin.Initialize( navigationService );
            }

            // Try to load the config; if it fails, it's not a big deal, there's always a saved one
            try
            {
                settings.Configuration = await serverAccess.LoadConfigurationAsync();
            }
            catch { }

            // SecureRequestHandler depends on the auth plugin, so it must be initialized after it
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();
        }
    }
}