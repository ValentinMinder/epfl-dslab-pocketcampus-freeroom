// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Uncomment this line if you are connected to the EPFL network (which, for now, means being at EPFL physically)
// #define IS_AT_EPFL

using System.Collections.Generic;
using System.Reflection;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using ThriftSharp;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Loads and provides access to the server configuration.
    /// </summary>
    public sealed class ServerAccess : IServerAccess
    {
        private const int ThriftConnectionTimeout = 30000; // in milliseconds
        private const string ServerAuthenticationHeader = "X-PC-AUTH-PCSESSID";

        // The format of the URL to get the current server configuration
        // The parameter is the app version
        private const string ServerConfigUrlFormat = "https://pocketcampus.epfl.ch/backend/get_config.php?platform=win&app_version={0}";

        // The format of the server URL
        // Parameters are the protocol and the port
#if DEBUG && IS_AT_EPFL
        private const string ThriftServerUrlFormat = "http://dslabpc36.epfl.ch:9090/v3r1";
#else
        private const string ThriftServerUrlFormat = "{0}://pocketcampus.epfl.ch:{1}/v3r1";
#endif
        // The format of a service URL
        // Parameters are the server URL and the service name
        private const string ThriftServiceUrlFormat = "{0}/{1}";


        private readonly IHttpClient _client;
        private readonly IServerSettings _settings;
        private readonly IDictionary<string, string> _headers;


        /// <summary>
        /// Creates a new ServerAccess.
        /// </summary>
        public ServerAccess( IHttpClient client, IServerSettings settings )
        {
            _client = client;
            _settings = settings;
            _headers = new Dictionary<string, string>();

            UpdateSessionHeader();
            _settings.ListenToProperty( x => x.Session, UpdateSessionHeader );
        }


        /// <summary>
        /// Asynchronously loads the server configuration.
        /// </summary>
        public async Task<ServerConfiguration> LoadConfigurationAsync()
        {
            string version = typeof( ServerAccess ).GetTypeInfo().Assembly.GetName().Version.ToString( 2 );
            string url = string.Format( ServerConfigUrlFormat, version );
            var res = await _client.GetAsync( url );
            return ServerConfiguration.Deserialize( res.Content );
        }

        /// <summary>
        /// Creates a ThriftCommunication for a plugin.
        /// </summary>
        public ThriftCommunication CreateCommunication( string pluginName )
        {
            string format = string.Format( ThriftServerUrlFormat, _settings.Configuration.Protocol, _settings.Configuration.Port );
            string url = string.Format( ThriftServiceUrlFormat, format, pluginName );
            return ThriftCommunication.Binary().OverHttp( url, ThriftConnectionTimeout, _headers );
        }

        /// <summary>
        /// Updates the HTTP headers sent with each request to add/remove/change the session.
        /// </summary>
        private void UpdateSessionHeader()
        {
            if ( _headers.ContainsKey( ServerAuthenticationHeader ) )
            {
                if ( _settings.Session == null )
                {
                    _headers.Remove( ServerAuthenticationHeader );
                }
                else
                {
                    _headers[ServerAuthenticationHeader] = _settings.Session;
                }
            }
            else if ( _settings.Session != null )
            {
                _headers.Add( ServerAuthenticationHeader, _settings.Session );
            }
        }
    }
}