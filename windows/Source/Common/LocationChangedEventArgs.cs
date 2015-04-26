// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;

namespace PocketCampus.Common
{
    /// <summary>
    /// Provides data for the <see cref="ILocationService.LocationChanged"/> event.
    /// </summary>
    public sealed class LocationChangedEventArgs : EventArgs
    {
        /// <summary>
        /// Gets the location.
        /// </summary>
        public GeoPosition Location { get; private set; }


        /// <summary>
        /// Creates a new LocationChangedEventArgs.
        /// </summary>
        public LocationChangedEventArgs( GeoPosition location )
        {
            Location = location;
        }
    }
}