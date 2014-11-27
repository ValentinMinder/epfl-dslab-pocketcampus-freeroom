// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Contains application-level settings.
    /// </summary>
    public interface IMainSettings : IServerSettings
    {
        /// <summary>
        /// Gets or sets the saved sessions.
        /// </summary>
        Dictionary<string, string> Sessions { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether the app should use a colored tile, as opposed to a white one.
        /// </summary>
        bool UseColoredTile { get; set; }
    }
}