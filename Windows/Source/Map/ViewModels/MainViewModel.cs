// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    [LogId( "/map" )]
    public sealed class MainViewModel : ViewModel<MapSearchRequest>, IDisposable
    {
        // The zoom level used when centering the map on the campus.
        private const int CampusZoomLevel = 16;
        // The coordinates of the campus center.
        private static readonly GeoPosition CampusPosition = new GeoPosition( 46.520533, 6.565012 );

        private readonly ILocationService _locationService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _settings;

        private GeoLocationStatus _locationStatus;
        private bool _isCenteredOnUser;


        public MapProperties Properties { get; private set; }

        public SearchProvider SearchProvider { get; private set; }

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

        [LogId( "ToggleCenterOnUser" )]
        [LogParameter( "IsCenteredOnUser" )]
        [LogValueConverter( typeof( ToggleCenterOnUserLogConverter ) )]
        public Command ToggleCenterOnUserCommand
        {
            get { return this.GetCommand( ToggleCenterOnUser, () => _settings.UseGeolocation ); }
        }

        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }


        public MainViewModel( ILocationService locationService, INavigationService navigationService,
                              IMapService mapService, IPluginSettings settings,
                              MapSearchRequest request )
        {
            _locationService = locationService;
            _navigationService = navigationService;
            _settings = settings;
            SearchProvider = new SearchProvider( mapService );

            _locationService.Ready += LocationService_Ready;
            _locationService.LocationChanged += LocationService_LocationChanged;
            _locationService.Error += LocationService_Error;

            Properties = new MapProperties();

            SearchProvider.ExecuteRequest( request );
        }


        public override void OnNavigatedTo()
        {
            if ( Properties.Center == null )
            {
                CenterOnCampus();
            }

            if ( _settings.UseGeolocation )
            {
                _locationService.IsEnabled = true;
            }
            else
            {
                _locationService.IsEnabled = false;
                Properties.UserPosition = null;
                LocationStatus = GeoLocationStatus.NotRequested;
            }
        }


        private void CenterOnCampus()
        {
            IsCenteredOnUser = false;
            Properties.ZoomLevel = CampusZoomLevel;
            Properties.Center = CampusPosition;
        }

        private void ToggleCenterOnUser()
        {
            IsCenteredOnUser = !IsCenteredOnUser;

            if ( IsCenteredOnUser )
            {
                Properties.UserPosition = _locationService.LastKnownLocation;
                Properties.Center = Properties.UserPosition;
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


        // Avoid memory leaks, since the geolocator is static
        public void Dispose()
        {
            _locationService.Ready -= LocationService_Ready;
            _locationService.LocationChanged -= LocationService_LocationChanged;
            _locationService.Error -= LocationService_Error;
        }


        // For logging purposes only
        private sealed class ToggleCenterOnUserLogConverter : ILogValueConverter
        {
            public string Convert( object value ) // the parameter is the current value of IsCenteredOnUser, we need the next one to log
            {
                return ( !( (bool) value ) ).ToString();
            }
        }
    }
}