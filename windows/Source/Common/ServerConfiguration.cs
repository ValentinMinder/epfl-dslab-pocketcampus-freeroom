// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Text;

namespace PocketCampus.Common
{
    /// <summary>
    /// Server configuration information.
    /// </summary>
    [DataContract]
    public sealed class ServerConfiguration
    {
        // The format of the server URL
        // Parameters are the protocol, the URL and the port
#if DEBUG
        private const string ServerBaseUrlFormat = "http://test-pocketcampus.epfl.ch:14610/v3r1/";
#else
        private const string ServerBaseUrlFormat = "{0}://{1}:{2}/v3r1/";
#endif

        /// <summary>
        /// Gets the protocol used to access the server.
        /// </summary>
        [DataMember( Name = "SERVER_PROTOCOL" )]
        public string Protocol { get; set; }

        /// <summary>
        /// Gets the address used to access the server.
        /// </summary>
        [DataMember( Name = "SERVER_ADDRESS" )]
        public string Address { get; set; }

        /// <summary>
        /// Gets the port used to access the server.
        /// </summary>
        [DataMember( Name = "SERVER_PORT" )]
        public int Port { get; set; }

        /// <summary>
        /// Gets the enabled plugins.
        /// </summary>
        /// <remarks>
        /// Plugins not on this list do not currently work, or have known bugs.
        /// They must be disabled.
        /// </remarks>
        [DataMember( Name = "ENABLED_PLUGINS" )]
        public string[] EnabledPlugins { get; set; }

        /// <summary>
        /// Gets the server's base URL.
        /// </summary>
        [IgnoreDataMember]
        public string ServerBaseUrl
        {
            get { return string.Format( ServerBaseUrlFormat, Protocol, Address, Port ); }
        }


        /// <summary>
        /// Creates a new ServerConfiguration.
        /// </summary>
        /// <remarks>
        /// For serialization purposes only.
        /// </remarks>
        public ServerConfiguration() { }

        /// <summary>
        /// Creates a new ServerConfiguration.
        /// </summary>
        public ServerConfiguration( string protocol, string address, int port, params string[] enabledPlugins )
        {
            Protocol = protocol;
            Address = address;
            Port = port;
            EnabledPlugins = enabledPlugins;
        }

        /// <summary>
        /// Deserializes a Configuration object from the specified JSON.
        /// </summary>
        public static ServerConfiguration Deserialize( string json )
        {
            var serializer = new DataContractJsonSerializer( typeof( ServerConfiguration ) );
            using ( var stream = new MemoryStream( Encoding.UTF8.GetBytes( json ) ) )
            {
                return (ServerConfiguration) serializer.ReadObject( stream );
            }
        }
    }
}