// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Satellite.Models;
using ThriftSharp;

// Plumbing for ISatelliteService

namespace PocketCampus.Satellite.Services
{
    public sealed class SatelliteService : ThriftServiceImplementation<ISatelliteService>, ISatelliteService
    {
        public SatelliteService( IServerAccess access )
            : base( access.CreateCommunication( "satellite" ) )
        {

        }

        public Task<BeersResponse> GetBeersAsync()
        {
            return CallAsync<BeersResponse>( x => x.GetBeersAsync );
        }
    }
}