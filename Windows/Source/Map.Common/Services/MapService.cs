// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Map.Models;
using ThriftSharp;

// Plumbing for IMapService

namespace PocketCampus.Map.Services
{
    public sealed class MapService : ThriftServiceImplementation<IMapService>, IMapService
    {
        public MapService( IServerAccess access )
            : base( access.CreateCommunication( "map" ) )
        {

        }

        public Task<MapLayer[]> GetLayersAsync()
        {
            return CallAsync<MapLayer[]>( x => x.GetLayersAsync );
        }

        public Task<MapItem[]> GetLayerItemsAsync( long layerId )
        {
            return CallAsync<long, MapItem[]>( x => x.GetLayerItemsAsync, layerId );
        }

        public Task<MapItem[]> SearchAsync( string query )
        {
            return CallAsync<string, MapItem[]>( x => x.SearchAsync, query );
        }
    }
}