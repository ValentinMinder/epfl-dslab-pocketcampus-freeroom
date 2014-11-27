// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using Windows.Devices.Geolocation;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Locates the current user on Earth.
    /// </summary>
    public sealed class LocationService : ILocationService
    {
        private const int MovementThreshold = 3; // meters

        private bool _isEnabled;

        private Geolocator _locator;
        private GeoPosition _lastKnownLocation;

        /// <summary>
        /// Gets or sets a value indicating whether the service is enabled.
        /// If true, the user's location is collected; make sure the user granted permission first.
        /// </summary>
        public bool IsEnabled
        {
            get { return _isEnabled; }
            set
            {
                if ( _isEnabled != value )
                {
                    _isEnabled = value;
                    OnIsEnabledChanged();
                }
            }
        }

        public GeoPosition LastKnownLocation
        {
            get { return _lastKnownLocation; }
        }

        /// <summary>
        /// Asynchronously gets the user's location.
        /// </summary>
        public async Task<Tuple<GeoPosition, GeoLocationStatus>> GetLocationAsync()
        {
            try
            {
                var geoPosition = await _locator.GetGeopositionAsync();
                _lastKnownLocation = PositionFromCoordinate( geoPosition.Coordinate );
                return Tuple.Create( _lastKnownLocation, GeoLocationStatus.Success );
            }
            catch
            {
                return Tuple.Create( (GeoPosition) null, GeoLocationStatus.Error );
            }
        }

        /// <summary>
        /// Occurs when the user's location changes.
        /// </summary>
        public event EventHandler<LocationChangedEventArgs> LocationChanged;
        private void OnLocationChanged( GeoPosition location )
        {
            var evt = LocationChanged;
            if ( evt != null )
            {
                evt( this, new LocationChangedEventArgs( location ) );
            }
        }

        /// <summary>
        /// Occurs when the location service is ready.
        /// </summary>
        public event EventHandler<EventArgs> Ready;
        private void OnReady()
        {
            var evt = Ready;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }

        /// <summary>
        /// Occurs when an error happens while locating the user.
        /// </summary>
        public event EventHandler<EventArgs> Error;
        private void OnError()
        {
            var evt = Error;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }

        /// <summary>
        /// Occurs when the status of the GeoLocator changes.
        /// </summary>
        private void Locator_StatusChanged( Geolocator sender, StatusChangedEventArgs args )
        {
            switch ( args.Status )
            {
                case PositionStatus.Disabled:
                case PositionStatus.NoData:
                case PositionStatus.NotAvailable:
                    OnError();
                    break;
                case PositionStatus.Ready:
                    OnReady();
                    break;
            }
        }

        /// <summary>
        /// Occurs when the user's position changes.
        /// </summary>
        private void Locator_PositionChanged( Geolocator sender, PositionChangedEventArgs args )
        {
            _lastKnownLocation = PositionFromCoordinate( args.Position.Coordinate );
            OnLocationChanged( _lastKnownLocation );
        }

        /// <summary>
        /// Occurs when the LocationService is enabled or disabled.
        /// </summary>
        private void OnIsEnabledChanged()
        {
            if ( IsEnabled )
            {
                _locator = new Geolocator();
                _locator.MovementThreshold = MovementThreshold;
                _locator.PositionChanged += Locator_PositionChanged;
                _locator.StatusChanged += Locator_StatusChanged;
            }
            else
            {
                _locator.PositionChanged -= Locator_PositionChanged;
                _locator.StatusChanged -= Locator_StatusChanged;
                _locator = null;
            }
        }


        /// <summary>
        /// Converts a Geocoordinate (Windows Phone) into a GeoPosition (PocketCampus).
        /// </summary>
        /// <param name="coord"></param>
        /// <returns></returns>
        private static GeoPosition PositionFromCoordinate( Geocoordinate coord )
        {
            return new GeoPosition( coord.Latitude, coord.Longitude, coord.Heading );
        }
    }
}