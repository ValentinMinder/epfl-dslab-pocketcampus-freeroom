// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ILocationService

#if DEBUG
using System;
using System.Threading.Tasks;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignLocationService : ILocationService
    {
        public bool IsEnabled { get; set; }

        public GeoPosition LastKnownLocation
        {
            get { return null; }
        }

        public Task<Tuple<GeoPosition, GeoLocationStatus>> GetLocationAsync()
        {
            return Task.FromResult( Tuple.Create( (GeoPosition) null, GeoLocationStatus.Error ) );
        }

#pragma warning disable 0067 // unused event
        public event EventHandler<LocationChangedEventArgs> LocationChanged;

        public event EventHandler<EventArgs> Ready;

        public event EventHandler<EventArgs> Error;
#pragma warning restore 0067
    }
}
#endif