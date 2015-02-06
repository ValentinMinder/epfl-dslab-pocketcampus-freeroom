// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    public interface ITileService
    {
        void CreateTile( IPlugin plugin, TileColoring coloring );
        void SetTileColoring( TileColoring coloring );
    }
}