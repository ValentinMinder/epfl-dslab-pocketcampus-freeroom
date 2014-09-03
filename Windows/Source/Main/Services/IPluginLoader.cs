// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Loads plugins from the currently referenced assemblies.
    /// </summary>
    public interface IPluginLoader
    {
        /// <summary>
        /// Asynchronously gets all available plugins.
        /// </summary>
        Task<IPlugin[]> GetPluginsAsync();
    }
}