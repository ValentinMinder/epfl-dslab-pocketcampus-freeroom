// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map
{
    public sealed class MapProperties : ObservableObject
    {
        private int _floor;
        private int _zoomLevel;
        private GeoPosition _userPosition;
        private GeoPosition _center;


        public int Floor
        {
            get { return _floor; }
            set
            {
                if ( value > _floor )
                {
                    Messenger.Send( new EventLogRequest( "IncreaseFloor", null ) );
                }
                else if ( value < _floor )
                {
                    Messenger.Send( new EventLogRequest( "DecreaseFloor", null ) );
                }

                SetProperty( ref _floor, value );
            }
        }

        public int ZoomLevel
        {
            get { return _zoomLevel; }
            set { SetProperty( ref _zoomLevel, value ); }
        }

        public GeoPosition UserPosition
        {
            get { return _userPosition; }
            set { SetProperty( ref _userPosition, value ); }
        }

        public GeoPosition Center
        {
            get { return _center; }
            set { SetProperty( ref _center, value ); }
        }
    }
}