// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Map.Models;
using ThriftSharp;

// Plumbing for IMapService

namespace PocketCampus.Map.Services
{
    public sealed class MapService : ThriftServiceImplementation<IMapService>, IMapService
    {
        public MapService( IServerAccess access ) : base( access.CreateCommunication( "map" ) ) { }

        public Task<MapItem[]> SearchAsync( string query, CancellationToken cancellationToken )
        {
            return CallAsync<string, CancellationToken, MapItem[]>( x => x.SearchAsync, query, cancellationToken );
        }
    }
}