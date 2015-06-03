// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.Common;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;
using Windows.Foundation;

namespace PocketCampus.Main
{
    public sealed class ProtocolHandler
    {
        public const string PocketCampusProtocol = "pocketcampus";

        // Logging stuff
        private const string CustomUriEventId = "OpenPocketCampusURL";
        private const string CustomUriScreenId = "/";

        // Constants for the parsing of PocketCampus URIs.
        private const string PluginSuffix = ".plugin.pocketcampus.org";

        private readonly IPlugin[] _plugins;
        private readonly INavigationService _navigationService;


        public ProtocolHandler( IPluginLoader pluginLoader, INavigationService navigationService )
        {
            _plugins = pluginLoader.GetPlugins();
            _navigationService = navigationService;
        }


        /// <summary>
        /// Navigates to the specified PocketCampus URI.
        /// </summary>
        public void NavigateToCustomUri( Uri uri )
        {
            Messenger.Send( new EventLogRequest( CustomUriEventId, uri.ToString(), CustomUriScreenId ) );

            var pluginAndParams = ParseQuery( uri );
            _plugins.First( p => p.Id.Equals( pluginAndParams.Item1, StringComparison.OrdinalIgnoreCase ) )
                    .NavigateTo( pluginAndParams.Item2, pluginAndParams.Item3, _navigationService );
        }

        /// <summary>
        /// Parses the specified query to get the plugin name, action name and parameters.
        /// </summary>
        private static Tuple<string, string, Dictionary<string, string>> ParseQuery( Uri uri )
        {
            var pluginName = uri.Host.Replace( PluginSuffix, "" );
            var actionName = uri.AbsolutePath.Substring( 1 ); // remove first '/'
            var parameters = string.IsNullOrEmpty( uri.Query ) ?
                             new Dictionary<string, string>()
                           : new WwwFormUrlDecoder( uri.Query ).ToDictionary( e => e.Name, e => e.Value );

            return Tuple.Create( pluginName, actionName, parameters );
        }
    }
}
