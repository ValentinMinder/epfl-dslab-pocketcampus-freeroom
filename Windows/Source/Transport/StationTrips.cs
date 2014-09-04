// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Transport.Models;
using PocketCampus.Transport.Services;
using ThinMvvm;

namespace PocketCampus.Transport
{
    /// <summary>
    /// Information about trips to a station.
    /// </summary>
    // HACK: Inherit from DataViewModel since it has everything we need
    public sealed class StationTrips : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly Station _from;

        private Trip[] _trips;

        /// <summary>
        /// Gets the station that is the destination of the trips.
        /// </summary>
        public Station Destination { get; private set; }

        /// <summary>
        /// Gets the trips.
        /// </summary>
        public Trip[] Trips
        {
            get { return _trips; }
            private set { SetProperty( ref _trips, value ); }
        }


        /// <summary>
        /// Creates a new StationTrips.
        /// </summary>
        public StationTrips( ITransportService transportService, Station from, Station to )
        {
            _transportService = transportService;
            _from = from;
            Destination = to;
        }

        public async void StartRefresh()
        {
            await TryRefreshAsync( true );
        }

        protected override async Task RefreshAsync( bool force, CancellationToken token )
        {
            var request = new TripSearchRequest
            {
                From = _from,
                To = Destination
            };

            var response = await _transportService.SearchTripsAsync( request, token );

            if ( response.Status != TransportStatus.Success )
            {
                throw new Exception( "An error occurred on the server while fetching trips." );
            }

            if ( !token.IsCancellationRequested )
            {
                Trips = response.Trips;
            }
        }
    }
}