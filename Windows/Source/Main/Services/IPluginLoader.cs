// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Loads plugins from the currently referenced assemblies.
    /// </summary>
    public interface IPluginLoader
    {
        /// <summary>
        /// Gets all available plugins.
        /// </summary>
        IPlugin[] GetPlugins();
    }
}