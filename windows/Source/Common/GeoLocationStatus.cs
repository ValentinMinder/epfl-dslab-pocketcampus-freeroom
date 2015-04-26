// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common
{
    /// <summary>
    /// Display-friendly status of a geolocation.
    /// </summary>
    public enum GeoLocationStatus
    {
        /// <summary>
        /// No geolocation was requested.
        /// </summary>
        NotRequested,

        /// <summary>
        /// The geolocation completed successfully.
        /// </summary>
        Success,

        /// <summary>
        /// An error occurred during the geolocation.
        /// </summary>
        Error
    }
}