// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Map.Models;
using ThriftSharp;

namespace PocketCampus.Map.Services
{
    [ThriftService( "MapService" )]
    public interface IMapService
    {
        [ThriftMethod( "search" )]
        Task<MapItem[]> SearchAsync( [ThriftParameter( 1, "query" )] string query, CancellationToken cancellationToken );
    }
}