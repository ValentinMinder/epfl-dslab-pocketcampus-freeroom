// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Map.Models;
using ThriftSharp;

namespace PocketCampus.Map.Services
{
    /// <summary>
    /// The map server service.
    /// </summary>
    [ThriftService( "MapService" )]
    public interface IMapService
    {
        /// <summary>
        /// Asynchronously searches the map items with the specified query.
        /// </summary>
        [ThriftMethod( "search" )]
        Task<MapItem[]> SearchAsync( [ThriftParameter( 1, "query" )] string query, CancellationToken cancellationToken );
    }
}