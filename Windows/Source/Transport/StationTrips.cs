// Copyright (c) PocketCampus.Org 2014-15
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
    // HACK: Inherit from DataViewModel since it has everything we need
    public sealed class StationTrips : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly Station _from;

        private Trip[] _trips;


        public Station Destination { get; private set; }

        public Trip[] Trips
        {
            get { return _trips; }
            private set { SetProperty( ref _trips, value ); }
        }


        public StationTrips( ITransportService transportService, Station from, Station to )
        {
            _transportService = transportService;
            _from = from;
            Destination = to;
        }


        public async void Refresh()
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