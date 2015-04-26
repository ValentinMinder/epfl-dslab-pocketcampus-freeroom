// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ITileService

#if DEBUG
using PocketCampus.Common;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignTileService : ITileService
    {
        public void CreateTile( IPlugin plugin, TileColoring coloring ) { }

        public void SetTileColoring( TileColoring coloring ) { }
    }
}
#endif