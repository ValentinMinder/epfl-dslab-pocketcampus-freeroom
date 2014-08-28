// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.ObjectModel;
using PocketCampus.Transport.Models;

namespace PocketCampus.Transport.Services
{
    /// <summary>
    /// Plugin-specific settings.
    /// </summary>
    public interface IPluginSettings
    {
        /// <summary>
        /// Gets or sets a value indicating whether to sort the stations
        /// according to their distance from the user.
        /// </summary>
        bool SortByPosition { get; set; }

        /// <summary>
        /// Gets or sets the stations.
        /// </summary>
        ObservableCollection<Station> Stations { get; set; }
    }
}