// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Main
{
    /// <summary>
    /// Search request for either a plugin name (or nothing).
    /// </summary>
    public sealed class ViewPluginRequest
    {
        /// <summary>
        /// Gets the plugin name, if any.
        /// </summary>
        public string PluginName { get; private set; }


        /// <summary>
        /// Creates an empty ViewPluginRequest.
        /// </summary>
        public ViewPluginRequest() { }

        /// <summary>
        /// Creates a ViewPluginRequest with the specified plugin name.
        /// </summary>
        public ViewPluginRequest( string pluginName )
        {
            PluginName = pluginName;
        }
    }
}