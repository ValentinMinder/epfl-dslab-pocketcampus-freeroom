// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Map.Models;

namespace PocketCampus.Map.Services.Design
{
    public sealed class DesignMapService : IMapService
    {
        public Task<MapItem[]> SearchAsync( string query, CancellationToken cancellationToken )
        {
            return Task.FromResult( new MapItem[0] );
        }
    }
}
#endif