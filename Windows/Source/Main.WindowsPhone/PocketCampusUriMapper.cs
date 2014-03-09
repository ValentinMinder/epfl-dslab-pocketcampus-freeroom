// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows.Navigation;
using PocketCampus.Common;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main
{
    /// <summary>
    /// The URI mapper for PocketCampus URIs.
    /// </summary>
    public sealed class PocketCampusUriMapper : UriMapperBase
    {
        // The key to the redirect parameter for Redirect.xaml.
        public const string RedirectRequestKey = "redirect";
        public const string PocketCampusProtocol = "pocketcampus";

        // Logging stuff
        private const string CustomUriEventId = "OpenPocketCampusURL";
        private const string CustomUriScreenId = "/";

        // Constants for the parsing of PocketCampus URIs.
        private const string PocketCampusPrefix = "pocketcampus://";
        private const string ProtocolPrefix = "/Protocol?encodedLaunchUri=";
        private const char PluginActionDelimiter = '/';
        private const string PluginSuffix = ".plugin.pocketcampus.org";
        private const char ActionParametersDelimiter = '?';
        private const char ParametersSeparator = '&';
        private const char KeyValueDelimiter = '=';

        private readonly IPlugin[] _plugins;


        /// <summary>
        /// Creates a new PocketCampusUriMapper.
        /// </summary>
        public PocketCampusUriMapper( IPlugin[] plugins )
        {
            _plugins = plugins;
        }


        /// <summary>
        /// Maps the specified URI.
        /// </summary>
        public override Uri MapUri( Uri uri )
        {
            string decodedUri = HttpUtility.UrlDecode( uri.ToString() );

            if ( decodedUri.StartsWith( ProtocolPrefix ) )
            {
                string newUri = decodedUri.Replace( ProtocolPrefix, "" );
                newUri = HttpUtility.UrlEncode( newUri );

                Messenger.Send( new EventLogRequest( CustomUriEventId, newUri, CustomUriScreenId ) );

                string newTarget = string.Format( "/PocketCampus.Main.WindowsPhone;component/Views/Redirect.xaml?{0}={1}", RedirectRequestKey, newUri );
                return new Uri( newTarget, UriKind.Relative );
            }
            return uri;
        }

        /// <summary>
        /// Gets the mappable part of a custom URI if it is one, or null otherwise.
        /// </summary>
        public bool IsCustomUri( string uri )
        {
            return uri.StartsWith( PocketCampusPrefix );
        }

        /// <summary>
        /// Navigates to the specified PocketCampus URI, or returns false if the URI is not a PocketCampus one.
        /// </summary>
        public void NavigateToCustomUri( string uri )
        {
            var pluginAndParams = ParseQuery( uri );
            _plugins.First( p => p.Id.Equals( pluginAndParams.Item1, StringComparison.OrdinalIgnoreCase ) )
                    .NavigateTo( pluginAndParams.Item2, pluginAndParams.Item3, App.NavigationService );
        }

        /// <summary>
        /// Parses the specified query to get the plugin name, action name and parameters, if it's a PocketCampus URI.
        /// </summary>
        private static Tuple<string, string, Dictionary<string, string>> ParseQuery( string uri )
        {
            // The URI we get from Windows Phone is URL-encoded...
            string query = HttpUtility.UrlDecode( uri );
            // ...and the original URI is URL-encoded too
            query = HttpUtility.UrlDecode( query );

            query = query.Replace( PocketCampusPrefix, "" );

            string[] parts = query.Split( PluginActionDelimiter );
            string pluginName = parts[0].Replace( PluginSuffix, "" );

            parts = parts[1].Split( ActionParametersDelimiter );
            string actionName = parts[0];

            if ( parts.Length > 0 )
            {
                var parameters = parts[1].Split( ParametersSeparator ).Select( s => s.Split( KeyValueDelimiter ) ).ToDictionary( s => s[0], s => s[1] );

                return Tuple.Create( pluginName, actionName, parameters );
            }

            return Tuple.Create( pluginName, actionName, new Dictionary<string, string>() );
        }
    }
}