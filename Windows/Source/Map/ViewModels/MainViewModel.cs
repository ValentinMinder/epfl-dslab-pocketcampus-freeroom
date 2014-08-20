// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Input;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Map.Models;
using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    /// <summary>
    /// The main ViewModel, with the map and search functionality.
    /// </summary>
    [LogId( "/map" )]
    public sealed class MainViewModel : DataViewModel<MapSearchRequest>
    {
        // The default zoom level.
        private const int DefaultZoomLevel = 17;
        // The zoom level used when centering the map on the campus.
        private const int CampusZoomLevel = 16;
        // The coordinates of the campus center.
        private static readonly GeoPosition CampusPosition = new GeoPosition( 46.520533, 6.565012 );

        private readonly IMapService _mapService;
        private readonly ILocationService _locationService;
        private readonly INavigationService _navigationService;
        private readonly IPluginSettings _settings;

        private GeoLocationStatus _locationStatus;
        private MapLayer[] _mapLayers;

        /// <summary>
        /// Gets the map properties.
        /// </summary>
        public MapProperties Properties { get; private set; }

        /// <summary>
        /// Gets the search provider.
        /// </summary>
        public SearchProvider SearchProvider { get; private set; }

        /// <summary>
        /// Gets the geo-location status.
        /// </summary>
        public GeoLocationStatus LocationStatus
        {
            get { return _locationStatus; }
            private set { SetProperty( ref _locationStatus, value ); }
        }

        /// <summary>
        /// Gets the map layers.
        /// </summary>
        public MapLayer[] MapLayers
        {
            get { return _mapLayers; }
            private set { SetProperty( ref _mapLayers, value ); }
        }

        /// <summary>
        /// Gets the command executed to center the map on the campus.
        /// </summary>
        [LogId( "CenterOnCampus" )]
        public Command CenterOnCampusCommand
        {
            get { return this.GetCommand( CenterOnCampus ); }
        }

        /// <summary>
        /// Gets the command executed to center the map on the user's position.
        /// </summary>
        [LogId( "CenterOnSelf" )]
        public ICommand CenterOnPositionCommand
        {
            get { return this.GetCommand( CenterOnPosition, () => _settings.UseGeolocation ); }
        }

        /// <summary>
        /// Gets the command executed to show the settings page.
        /// </summary>
        [LogId( "OpenSettings" )]
        public Command ViewSettingsCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( ILocationService locationService, INavigationService navigationService,
                              IMapService mapService, IPluginSettings settings,
                              SearchProvider searchProvider,
                              MapSearchRequest request )
        {
            _mapService = mapService;
            _locationService = locationService;
            _navigationService = navigationService;
            _settings = settings;
            SearchProvider = searchProvider;

            _locationService.Ready += LocationService_Ready;
            _locationService.LocationChanged += LocationService_LocationChanged;
            _locationService.Error += LocationService_Error;

            Properties = new MapProperties { ZoomLevel = DefaultZoomLevel };

            SearchProvider.ExecuteRequest( request );
        }


        /// <summary>
        /// Executed when the user opens the plugin, or comes back from the settings page.
        /// </summary>
        protected override async Task RefreshAsync( bool force, CancellationToken token )
        {
            if ( force )
            {
                // TODO: Find a correct way to display that
                await Task.Delay( 0 ); // make the compiler happy for now
                //var layers = (IEnumerable<MapLayer>) await _mapService.GetLayersAsync();
                //layers = layers.Where( l => l.CanDisplay );
                //foreach ( var layer in layers )
                //{
                //    layer.Items = await _mapService.GetLayerItemsAsync( layer.Id, token );
                //    foreach ( var item in layer.Items )
                //    {
                //        item.ImageUrl = "http://pocketcampus.epfl.ch/" + layer.ImageUrl;
                //    }
                //}

                //MapLayers = layers.ToArray();
            }

            if ( Properties.Center == null )
            {
                Properties.Center = CampusPosition;
            }

            if ( _settings.UseGeolocation )
            {
                _locationService.IsEnabled = true;

                // This task should not be awaited; assigning it to a variable removes the compiler warning
                var _ = _locationService.GetLocationAsync().ContinueWith( task =>
                {
                    Properties.UserPosition = task.Result.Item1;
                    LocationStatus = task.Result.Item2;

                    Properties.Center = Properties.UserPosition;
                } );
            }
            else
            {
                _locationService.IsEnabled = false;
                Properties.UserPosition = null;
                LocationStatus = GeoLocationStatus.NotRequested;
            }
        }

        /// <summary>
        /// Centers the map on the EPFL campus.
        /// </summary>
        private void CenterOnCampus()
        {
            Properties.ZoomLevel = CampusZoomLevel;
            Properties.Center = CampusPosition;
        }

        /// <summary>
        /// Centers the map on the user's position.
        /// </summary>
        private void CenterOnPosition()
        {
            Properties.ZoomLevel = DefaultZoomLevel;
            Properties.Center = Properties.UserPosition;
        }

        /// <summary>
        /// Executed when the location service is ready to track the user.
        /// </summary>
        private void LocationService_Ready( object sender, EventArgs e )
        {
            LocationStatus = GeoLocationStatus.Success;
        }

        /// <summary>
        /// Executed when the user's position changes.
        /// </summary>
        private void LocationService_LocationChanged( object sender, LocationChangedEventArgs e )
        {
            Properties.UserPosition = e.Location;
        }

        /// <summary>
        /// Executed when the location service encounters a problem.
        /// </summary>
        private void LocationService_Error( object sender, EventArgs e )
        {
            Properties.UserPosition = null;
            LocationStatus = GeoLocationStatus.Error;
        }
    }
}