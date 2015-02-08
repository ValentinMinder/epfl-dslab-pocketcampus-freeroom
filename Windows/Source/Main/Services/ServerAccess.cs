// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Reflection;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.Main.Services
{
    public sealed class ServerAccess : IServerAccess
    {
        private const int ThriftConnectionTimeout = 30000; // in milliseconds

        // The format of the URL to get the current server configuration
        // The parameter is the app version
        private const string ServerConfigUrlFormat = "https://pocketcampus.epfl.ch/backend/get_config.php?platform=win&app_version={0}";

        private readonly IHttpClient _client;
        private readonly IServerSettings _settings;
        private readonly IHttpHeaders _headers;


        public ServerAccess( IHttpClient client, IServerSettings settings, IHttpHeaders headers )
        {
            _client = client;
            _settings = settings;
            _headers = headers;
        }


        public async Task<ServerConfiguration> LoadConfigurationAsync()
        {
            string version = typeof( ServerAccess ).GetTypeInfo().Assembly.GetName().Version.ToString( 2 );
            string url = string.Format( ServerConfigUrlFormat, version );
            var res = await _client.GetAsync( url );
            return ServerConfiguration.Deserialize( res.Content );
        }

        public ThriftCommunication CreateCommunication( string pluginName )
        {
            string url = _settings.Configuration.ServerBaseUrl + pluginName;
            return ThriftCommunication.Binary().OverHttp( url, ThriftConnectionTimeout, _headers.Current );
        }
    }
}