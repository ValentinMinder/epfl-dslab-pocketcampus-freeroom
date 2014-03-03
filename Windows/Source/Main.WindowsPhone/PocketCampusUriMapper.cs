using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows.Navigation;
using PocketCampus.Common;

namespace PocketCampus.Main
{
    public sealed class PocketCampusUriMapper : UriMapperBase
    {
        public const string RedirectRequestKey = "redirect";

        private const string PocketCampusProtocol = "pocketcampus://";
        private const string ProtocolPrefix = "/Protocol?encodedLaunchUri=";
        private const char PluginActionDelimiter = '/';
        private const string PluginSuffix = ".plugin.pocketcampus.org";
        private const char ActionParametersDelimiter = '?';
        private const char ParametersSeparator = '&';
        private const char KeyValueDelimiter = '=';

        private readonly IPlugin[] _plugins;

        public PocketCampusUriMapper( IPlugin[] plugins )
        {
            _plugins = plugins;
        }

        public override Uri MapUri( Uri uri )
        {
            string decodedUri = HttpUtility.UrlDecode( uri.ToString() );

            if ( decodedUri.StartsWith( ProtocolPrefix ) )
            {
                string newTarget = string.Format( "/PocketCampus.Main.WindowsPhone;component/Views/Redirect.xaml?{0}={1}",
                                                  RedirectRequestKey, decodedUri.Replace( ProtocolPrefix, "" ) );
                return new Uri( newTarget, UriKind.Relative );
            }
            return uri;
        }

        public bool NavigateToCustomUri( string uri )
        {
            var pluginAndParams = ParseQuery( uri );
            if ( pluginAndParams == null )
            {
                return false;
            }

            _plugins.First( p => p.Id.Equals( pluginAndParams.Item1, StringComparison.OrdinalIgnoreCase ) )
                    .NavigateTo( pluginAndParams.Item2, pluginAndParams.Item3, App.NavigationService );
            return true;
        }

        private static Tuple<string, string, Dictionary<string, string>> ParseQuery( string uri )
        {
            // The URI we get from Windows Phone is URL-encoded...
            string query = HttpUtility.UrlDecode( uri );
            // ...and the original URI is URL-encoded too
            query = HttpUtility.UrlDecode( query );

            if ( query.StartsWith( PocketCampusProtocol ) )
            {
                query = query.Replace( PocketCampusProtocol, "" );

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
            return null;
        }
    }
}