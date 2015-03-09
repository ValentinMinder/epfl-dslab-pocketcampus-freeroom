// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using Windows.Devices.Geolocation;

namespace PocketCampus.Main.Services
{
    public sealed class LocationService : ILocationService
    {
        private const int MovementThreshold = 3; // meters

        private bool _isEnabled;

        private Geolocator _locator;
        private GeoPosition _lastKnownLocation;


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

        public event EventHandler<LocationChangedEventArgs> LocationChanged;
        private void OnLocationChanged( GeoPosition location )
        {
            var evt = LocationChanged;
            if ( evt != null )
            {
                evt( this, new LocationChangedEventArgs( location ) );
            }
        }

        public event EventHandler<EventArgs> Ready;
        private void OnReady()
        {
            var evt = Ready;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }

        public event EventHandler<EventArgs> Error;
        private void OnError()
        {
            var evt = Error;
            if ( evt != null )
            {
                evt( this, EventArgs.Empty );
            }
        }


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

        private void Locator_PositionChanged( Geolocator sender, PositionChangedEventArgs args )
        {
            _lastKnownLocation = PositionFromCoordinate( args.Position.Coordinate );
            OnLocationChanged( _lastKnownLocation );
        }

        private void OnIsEnabledChanged()
        {
            if ( IsEnabled )
            {
                _locator = new Geolocator
                {
                    MovementThreshold = MovementThreshold
                };
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
        private static GeoPosition PositionFromCoordinate( Geocoordinate coord )
        {
            return new GeoPosition( coord.Point.Position.Latitude, coord.Point.Position.Longitude );
        }
    }
}