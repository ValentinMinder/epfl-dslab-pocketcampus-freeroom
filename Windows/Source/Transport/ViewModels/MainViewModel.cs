// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Transport.Models;
using PocketCampus.Transport.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Transport.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/transport" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly ITransportService _transportService;
        private readonly INavigationService _navigationService;
        private readonly ILocationService _locationService;

        private GeoLocationStatus _locationStatus;
        private StationTrips[] _trips;
        private Station _selectedStation;

        /// <summary>
        /// Gets the settings.
        /// </summary>
        public IPluginSettings Settings { get; private set; }

        /// <summary>
        /// Gets the geo-location status.
        /// </summary>
        public GeoLocationStatus LocationStatus
        {
            get { return _locationStatus; }
            private set { SetProperty( ref _locationStatus, value ); }
        }

        /// <summary>
        /// Gets the trips.
        /// </summary>
        public StationTrips[] Trips
        {
            get { return _trips; }
            private set { SetProperty( ref _trips, value ); }
        }

        /// <summary>
        /// Gets or sets the selected station.
        /// </summary>
        public Station SelectedStation
        {
            get { return _selectedStation; }
            set
            {
                if ( _selectedStation != value )
                {
                    SetProperty( ref _selectedStation, value );
                    TryRefreshAsync( false );
                }
            }
        }

        /// <summary>
        /// Gets the command executed to add a station.
        /// </summary>
        [LogId( "AddStation" )]
        public Command AddStationCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<AddStationViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to remove a station.
        /// </summary>
        [LogId( "RemoveStation" )]
        public Command<Station> RemoveStationCommand
        {
            get { return this.GetCommand<Station>( RemoveStation ); }
        }

        /// <summary>
        /// Gets the command executed to view the settings.
        /// </summary>
        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( ITransportService transportService, IPluginSettings settings,
                              INavigationService navigationService, ILocationService locationService )
        {
            _transportService = transportService;
            Settings = settings;
            _navigationService = navigationService;
            _locationService = locationService;
        }


        /// <summary>
        /// Executed when the user navigates to this ViewModel, either by opening the plugin
        /// or by coming back from the settings.
        /// </summary>
        public override async Task OnNavigatedToAsync()
        {
            _locationService.IsEnabled = Settings.SortByPosition;
            await base.OnNavigatedToAsync();
        }

        /// <summary>
        /// Refreshes the data.
        /// </summary>
        protected override async Task RefreshAsync( bool force, CancellationToken token )
        {
            if ( Settings.Stations.Count == 0 )
            {
                var defaultStationsResponse = await _transportService.GetDefaultStationsAsync( token );

                if ( defaultStationsResponse.Status != TransportStatus.Success )
                {
                    throw new Exception( "An error occurred on the server while fetching the default stations." );
                }

                Settings.Stations = new ObservableCollection<Station>( defaultStationsResponse.Stations );
            }

            if ( SelectedStation == null && Settings.SortByPosition )
            {
                var locationAndStatus = await _locationService.GetLocationAsync();
                var location = locationAndStatus.Item1;
                LocationStatus = locationAndStatus.Item2;

                if ( LocationStatus != GeoLocationStatus.Error )
                {
                    SelectedStation = Settings.Stations.OrderBy( s => s.Position.DistanceTo( location ) ).First();
                }
            }
            if ( SelectedStation == null )
            {
                SelectedStation = Settings.Stations.First();
            }

            if ( !token.IsCancellationRequested )
            {
                Trips = Settings.Stations
                                .Where( s => s != SelectedStation )
                                .Select( to => new StationTrips( _transportService, SelectedStation, to ) )
                                .ToArray();

                foreach ( var trip in Trips )
                {
                    trip.StartRefresh();
                }
            }
        }

        /// <summary>
        /// Removes the specified station from the settings.
        /// </summary>
        private void RemoveStation( Station station )
        {
            Settings.Stations.Remove( station );
            Trips = Trips.Where( t => t.Destination != station ).ToArray();
        }
    }
}