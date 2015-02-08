// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Satellite.Models;
using ThriftSharp;

// Plumbing for ISatelliteService

namespace PocketCampus.Satellite.Services
{
    public sealed class SatelliteService : ThriftServiceImplementation<ISatelliteService>, ISatelliteService
    {
        public SatelliteService( IServerAccess access ) : base( access.CreateCommunication( "satellite" ) ) { }

        public Task<BeersResponse> GetBeersAsync( CancellationToken cancellationToken )
        {
            return CallAsync<CancellationToken, BeersResponse>( x => x.GetBeersAsync, cancellationToken );
        }
    }
}