// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using ThriftSharp;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Loads the server configuration and provides access to the server.
    /// </summary>
    public interface IServerAccess
    {
        /// <summary>
        /// Asynchronously loads the server configuration.
        /// </summary>
        Task<ServerConfiguration> LoadConfigurationAsync();

        /// <summary>
        /// Creates a ThriftCommunication for a plugin.
        /// </summary>
        ThriftCommunication CreateCommunication( string pluginName );
    }
}