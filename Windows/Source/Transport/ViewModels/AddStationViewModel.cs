// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Transport.Models;
using PocketCampus.Transport.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Transport.ViewModels
{
    [LogId( "/transport/addStation" )]
    public sealed class AddStationViewModel : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly ILocationService _locationService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _pluginSettings;

        private string _query;
        private Station[] _stations;


        public string Query
        {
            get { return _query; }
            set { SetProperty( ref _query, value ); }
        }

        public Station[] Stations
        {
            get { return _stations; }
            private set { SetProperty( ref _stations, value ); }
        }


        [LogId( "Add" )]
        [LogParameter( "$Param.Name" )]
        public Command<Station> AddStationCommand
        {
            get { return this.GetCommand<Station>( AddStation ); }
        }

        [LogId( "AddByName" )]
        [LogParameter( "$Param" )]
        public AsyncCommand<string> AddStationByNameCommand
        {
            get { return this.GetAsyncCommand<string>( AddStationByNameAsync ); }
        }


        public AddStationViewModel( ITransportService transportService, ILocationService locationService,
                                    INavigationService navigationService, IPluginSettings pluginSettings )
        {
            _transportService = transportService;
            _locationService = locationService;
            _navigationService = navigationService;
            _pluginSettings = pluginSettings;

            this.ListenToProperty( x => x.Query, OnQueryChanged );
        }


        private async void OnQueryChanged()
        {
            await TryExecuteAsync( async _ =>
            {
                var request = new StationSearchRequest
                {
                    StationName = Query,
                    UserLocation = _locationService.LastKnownLocation == null ? null : new GeoPoint
                    {
                        Latitude = _locationService.LastKnownLocation.Latitude,
                        Longitude = _locationService.LastKnownLocation.Longitude
                    }
                };

                // Don't use the provided token, having a bunch of OperationCanceledExceptions is too slow
                var response = await _transportService.SearchStationsAsync( request, CancellationToken.None );

                if ( response.Status != TransportStatus.Success )
                {
                    throw new Exception( "An error occurred on the server while fetching stations." );
                }

                Stations = response.Stations;
            } );
        }

        private void AddStation( Station station )
        {
            if ( _pluginSettings.Stations.All( s => s.Name != station.Name ) )
            {
                _pluginSettings.Stations.Add( station );
            }

            _navigationService.NavigateBack();
        }

        private async Task AddStationByNameAsync( string stationName )
        {
            if ( _pluginSettings.Stations.Any( s => s.Name == stationName ) )
            {
                _navigationService.NavigateBack();
            }

            await TryExecuteAsync( async token =>
            {
                var request = new StationSearchRequest
                {
                    StationName = stationName
                };

                var response = await _transportService.SearchStationsAsync( request, token );

                if ( response.Status != TransportStatus.Success )
                {
                    throw new Exception( "An error occurred on the server while fetching stations." );
                }

                _pluginSettings.Stations.Add( response.Stations[0] );
                _navigationService.NavigateBack();
            } );
        }
    }
}