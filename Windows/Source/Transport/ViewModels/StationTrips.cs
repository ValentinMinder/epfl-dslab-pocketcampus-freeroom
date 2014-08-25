// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Transport.Models;

namespace PocketCampus.Transport.ViewModels
{
    /// <summary>
    /// Information about trips to a station.
    /// </summary>
    public sealed class StationTrips
    {
        /// <summary>
        /// Gets the station that is the destination of the trips.
        /// </summary>
        public Station Station { get; private set; }

        /// <summary>
        /// Gets the trips.
        /// </summary>
        public Trip[] Trips { get; private set; }


        /// <summary>
        /// Creates a new StationTrips.
        /// </summary>
        public StationTrips( Station station, Trip[] trips )
        {
            Station = station;
            Trips = trips;
        }
    }
}