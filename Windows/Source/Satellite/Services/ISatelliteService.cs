// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Satellite.Models;
using ThriftSharp;

namespace PocketCampus.Satellite.Services
{
    /// <summary>
    /// The Satellite server service.
    /// </summary>
    [ThriftService( "SatelliteService" )]
    public interface ISatelliteService
    {
        /// <summary>
        /// Asynchronously gets Satellite's beer menu.
        /// </summary>
        [ThriftMethod( "getBeers" )]
        Task<BeersResponse> GetBeersAsync( CancellationToken cancellationToken );
    }
}