// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using Windows.UI.StartScreen;

namespace PocketCampus.Main.Services
{
    public sealed class TileService : ITileService
    {
        public async void CreateTile( IPlugin plugin )
        {
            var winPlugin = (IWindowsRuntimePlugin) plugin;
            // TODO icon
            var tile = new SecondaryTile( plugin.Id, winPlugin.Name, plugin.Id, null, TileSize.Square150x150 );
            await tile.RequestCreateAsync();
        }

        public void SetTileColoring( bool useColor )
        {
            // TODO
        }
    }
}