// Copyright (c) PocketCampus.Org 2014
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
        /// Gets or sets the currently used server configuration.
        /// </summary>
        ServerConfiguration CurrentConfiguration { get; set; }

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