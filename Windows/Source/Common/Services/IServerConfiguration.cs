// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using ThriftSharp;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Loads and provides access to the server configuration.
    /// </summary>
    public interface IServerConfiguration
    {
        /// <summary>
        /// Asynchronously loads the server configuration.
        /// </summary>
        /// <remarks>
        /// This method must be called before anything else.
        /// </remarks>
        Task LoadAsync();

        /// <summary>
        /// Gets the enabled plugins.
        /// </summary>
        /// <remarks>
        /// Plugins not on this list do not currently work, or have known bugs.
        /// They must be disabled.
        /// </remarks>
        string[] EnabledPlugins { get; }

        /// <summary>
        /// Creates a ThriftCommunication for a plugin.
        /// </summary>
        ThriftCommunication CreateCommunication( string pluginName );
    }
}