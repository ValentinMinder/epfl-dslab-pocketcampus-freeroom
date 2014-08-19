// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Creates "tiles" on the user's home screen.
    /// </summary>
    public interface ITileService
    {
        /// <summary>
        /// Creates a tile for the specified plugin.
        /// </summary>
        void CreateTile( IPlugin plugin );

        /// <summary>
        /// Sets the tile coloring; either colored, or white.
        /// </summary>
        void SetTileColoring( bool useColor );
    }
}