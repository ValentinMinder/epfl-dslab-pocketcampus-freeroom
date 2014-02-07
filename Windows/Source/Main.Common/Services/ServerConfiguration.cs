// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Uncomment this line if you are connected to the EPFL network (which, for now, means being at EPFL physically)
// #define IS_AT_EPFL

using System;
using System.IO;
using System.Reflection;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Loads and provides access to the server configuration.
    /// </summary>
    public sealed class ServerConfiguration : IServerConfiguration
    {
        private const int ThriftConnectionTimeout = 30000; // in milliseconds

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
        private const string ThriftDefaultServerUrlFormat = "https://pocketcampus.epfl.ch:4433/v3r1";
        // The format of a service URL
        // Parameters are the server URL and the service name
        private const string ThriftServiceUrlFormat = "{0}/{1}";


        private readonly IHttpClient _client;

        private string[] _enabledPlugins;
        private string _thriftServiceFormat;


        /// <summary>
        /// Creates a new ServerConfiguration.
        /// </summary>
        public ServerConfiguration( IHttpClient client )
        {
            _client = client;
        }


        /// <summary>
        /// Asynchronously loads the server configuration.
        /// </summary>
        public async Task LoadAsync()
        {
            var res = await _client.GetAsync( ServerConfigUrlFormat );
            var config = Configuration.Deserialize( res.Content );

            string version = typeof( ServerConfiguration ).GetTypeInfo().Assembly.GetName().Version.ToString( 2 );
            string url = string.Format( ServerConfigUrlFormat, version );

            _thriftServiceFormat = string.Format( ThriftServerUrlFormat, config.Protocol, config.Port );
            _enabledPlugins = config.EnabledPlugins;
        }

        /// <summary>
        /// Gets the enabled plugins.
        /// </summary>
        public string[] EnabledPlugins
        {
            get
            {
                if ( _enabledPlugins == null )
                {
                    throw new InvalidOperationException( "Call LoadAsync() first." );
                }
                return _enabledPlugins;
            }
        }

        /// <summary>
        /// Creates a ThriftCommunication for a plugin.
        /// </summary>
        public ThriftCommunication CreateCommunication( string pluginName )
        {
            string format = _thriftServiceFormat ?? ThriftDefaultServerUrlFormat;
            string url = string.Format( ThriftServiceUrlFormat, format, pluginName );
            return ThriftCommunication.Binary()
                                      .OverHttp( url, ThriftConnectionTimeout );
        }

        [DataContract]
        private sealed class Configuration
        {
            [DataMember( Name = "SERVER_PROTOCOL" )]
            public string Protocol { get; private set; }

            [DataMember( Name = "SERVER_PORT" )]
            public int Port { get; private set; }

            [DataMember( Name = "ENABLED_PLUGINS" )]
            public string[] EnabledPlugins { get; private set; }

            public static Configuration Deserialize( string json )
            {
                var serializer = new DataContractJsonSerializer( typeof( Configuration ) );
                return (Configuration) serializer.ReadObject( new MemoryStream( Encoding.UTF8.GetBytes( json ) ) );
            }
        }
    }
}