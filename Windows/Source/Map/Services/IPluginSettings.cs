// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;

namespace PocketCampus.Map.Services
{
    /// <summary>
    /// Settings for the map plugin.
    /// </summary>
    public interface IPluginSettings : INotifyPropertyChanged
    {
        /// <summary>
        /// Gets or sets a value indicating whether the map should display and use the user's position.
        /// </summary>
        bool UseGeolocation { get; set; }
    }
}