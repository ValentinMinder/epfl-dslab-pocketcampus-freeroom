// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.Map.ViewModels
{
    /// <summary>
    /// Contains map properties used to display a map.
    /// </summary>
    public sealed class MapProperties : ObservableObject
    {
        private int _buildingsLevel;
        private int _zoomLevel;
        private GeoPosition _userPosition;
        private GeoPosition _center;

        /// <summary>
        /// Gets or sets the map's buildings level.
        /// </summary>
        public int BuildingsLevel
        {
            get { return _buildingsLevel; }
            set { SetProperty( ref _buildingsLevel, value ); }
        }

        /// <summary>
        /// Gets or sets the map's zoom level.
        /// </summary>
        public int ZoomLevel
        {
            get { return _zoomLevel; }
            set { SetProperty( ref _zoomLevel, value ); }
        }

        /// <summary>
        /// Gets or sets the user's current position.
        /// </summary>
        public GeoPosition UserPosition
        {
            get { return _userPosition; }
            set { SetProperty( ref _userPosition, value ); }
        }

        /// <summary>
        /// Gets or sets the map's center.
        /// </summary>
        public GeoPosition Center
        {
            get { return _center; }
            set { SetProperty( ref _center, value ); }
        }
    }
}