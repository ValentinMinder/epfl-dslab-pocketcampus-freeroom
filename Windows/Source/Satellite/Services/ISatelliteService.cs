// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Satellite.Models;
using ThriftSharp;

namespace PocketCampus.Satellite.Services
{
    [ThriftService( "SatelliteService" )]
    public interface ISatelliteService
    {
        [ThriftMethod( "getBeers" )]
        Task<BeersResponse> GetBeersAsync( CancellationToken cancellationToken );
    }
}