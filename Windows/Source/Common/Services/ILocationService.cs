// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Locates the current user on Earth.
    /// </summary>
    public interface ILocationService
    {
        /// <summary>
        /// Gets or sets a value indicating whether the service is enabled.
        /// If true, the user's location is collected; make sure the user granted permission first.
        /// </summary>
        bool IsEnabled { get; set; }

        /// <summary>
        /// Gets the user's last known location, if any.
        /// </summary>
        GeoPosition LastKnownLocation { get; }

        /// <summary>
        /// Asynchronously gets the user's location.
        /// </summary>
        Task<Tuple<GeoPosition, GeoLocationStatus>> GetLocationAsync();

        /// <summary>
        /// Occurs when the user's location changes.
        /// </summary>
        event EventHandler<LocationChangedEventArgs> LocationChanged;

        /// <summary>
        /// Occurs when the location service is ready.
        /// </summary>
        event EventHandler<EventArgs> Ready;

        /// <summary>
        /// Occurs when an error happens while locating the user.
        /// </summary>
        event EventHandler<EventArgs> Error;
    }
}