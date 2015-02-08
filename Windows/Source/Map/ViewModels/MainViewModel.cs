// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Map.Models;
using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    [LogId( "/map" )]
    public sealed class MainViewModel : DataViewModel<MapSearchRequest>, IDisposable
    {
        // If the user toggled centering the map on their position, moving this far will toggle it off (in meters)
        private const double StopCenterOnUserThreshold = 5.0;
        // The zoom level used when centering the map on the campus.
        private const int CampusZoomLevel = 16;
        // The coordinates of the campus center.
        private static readonly GeoPosition CampusPosition = new GeoPosition( 46.520533, 6.565012 );

        private readonly IMapService _mapService;
        private readonly ILocationService _locationService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _settings;

        private string _query;
        private SearchStatus _searchStatus;
        private MapItem[] _searchResults;
        private GeoLocationStatus _locationStatus;
        private bool _isCenteredOnUser;


        public MapProperties Properties { get; private set; }

        public string Query
        {
            get { return _query; }
            set { SetProperty( ref _query, value ); }
        }

        public SearchStatus SearchStatus
        {
            get { return _searchStatus; }
            private set { SetProperty( ref _searchStatus, value ); }
        }

        public MapItem[] SearchResults
        {
            get { return _searchResults; }
            private set { SetProperty( ref _searchResults, value ); }
        }

        public GeoLocationStatus LocationStatus
        {
            get { return _locationStatus; }
            private set { SetProperty( ref _locationStatus, value ); }
        }

        public bool IsCenteredOnUser
        {
            get { return _isCenteredOnUser; }
            set { SetProperty( ref _isCenteredOnUser, value ); }
        }

        [LogId( "CenterOnCampus" )]
        public Command CenterOnCampusCommand
        {
            get { return this.GetCommand( CenterOnCampus ); }
        }

        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        [LogId( "Search" )]
        [LogParameter( "Query" )]
        public AsyncCommand SearchCommand
        {
            get { return this.GetAsyncCommand( () => SearchAsync( Query ) ); }
        }


        public MainViewModel( ILocationService locationService, INavigationService navigationService,
                              IMapService mapService, IPluginSettings settings,
                              MapSearchRequest request )
        {
            _mapService = mapService;
            _locationService = locationService;
            _navigationService = navigationService;
            _settings = settings;

            _locationService.Ready += LocationService_Ready;
            _locationService.LocationChanged += LocationService_LocationChanged;
            _locationService.Error += LocationService_Error;

            Properties = new MapProperties();

            ExecuteRequest( request );

            this.ListenToProperty( x => x.IsCenteredOnUser, IsCenterdOnUserChanged );
            Properties.ListenToProperty( x => x.Center, OnCenterChanged );
        }


        public override Task OnNavigatedToAsync()
        {
            // HACK: No call to base, we don't want the "try to refresh on the first navigation" behavior here
            //       Inheriting from DataViewModel is weird anyway...

            if ( Properties.Center == null )
            {
                CenterOnCampus();
            }

            _locationService.IsEnabled = _settings.UseGeolocation;
            if ( !_settings.UseGeolocation )
            {
                Properties.UserPosition = null;
                LocationStatus = GeoLocationStatus.NotRequested;
            }

            return Task.FromResult( 0 );
        }

        private async void ExecuteRequest( MapSearchRequest request )
        {
            if ( request.Query != null )
            {
                Query = request.Query;
                await SearchAsync( request.Query );
            }
            else if ( request.Item != null )
            {
                SearchResults = new[] { request.Item };
            }
        }

        private Task SearchAsync( string query )
        {
            return TryExecuteAsync( async token =>
            {
                var results = await _mapService.SearchAsync( query, token );
                var uniqueResult = results.FirstOrDefault( r => NameNormalizer.AreRoomNamesEqual( r.Name, query ) );

                if ( !token.IsCancellationRequested )
                {
                    SearchStatus = results.Length == 0 ? SearchStatus.NoResults : SearchStatus.Finished;
                    SearchResults = uniqueResult == null ? results : new[] { uniqueResult };
                }
            } );
        }

        private void CenterOnCampus()
        {
            IsCenteredOnUser = false;
            Properties.ZoomLevel = CampusZoomLevel;
            Properties.Center = CampusPosition;
        }

        private void IsCenterdOnUserChanged()
        {
            Messenger.Send( new EventLogRequest( "ToggleCenterOnUser", IsCenteredOnUser.ToString() ) );

            if ( IsCenteredOnUser )
            {
                Properties.UserPosition = _locationService.LastKnownLocation;
                Properties.Center = Properties.UserPosition;
            }
        }

        private void OnCenterChanged()
        {
            if ( Properties.Center == null || Properties.UserPosition == null )
            {
                // when loading the map, or if geolocation is disabled
                return;
            }

            if ( Properties.Center.DistanceTo( Properties.UserPosition ) >= StopCenterOnUserThreshold )
            {
                IsCenteredOnUser = false;
            }
        }

        private void LocationService_Ready( object sender, EventArgs e )
        {
            LocationStatus = GeoLocationStatus.Success;
        }

        private void LocationService_LocationChanged( object sender, LocationChangedEventArgs e )
        {
            Properties.UserPosition = e.Location;
            if ( IsCenteredOnUser )
            {
                Properties.Center = Properties.UserPosition;
            }
        }

        private void LocationService_Error( object sender, EventArgs e )
        {
            Properties.UserPosition = null;
            LocationStatus = GeoLocationStatus.Error;
        }

        // Avoid memory leaks
        public new void Dispose()
        {
            base.Dispose();
            _locationService.Ready -= LocationService_Ready;
            _locationService.LocationChanged -= LocationService_LocationChanged;
            _locationService.Error -= LocationService_Error;
        }
    }
}