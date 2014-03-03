// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using Microsoft.Phone.Shell;
using PocketCampus.Common;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Creates Live Tiles on the user's home screen.
    /// </summary>
    public sealed class TileCreator : ITileCreator
    {
        public const string PluginKey = "plugin";

        /// <summary>
        /// Creates a Live Tile for the specified plugin.
        /// </summary>
        public void CreateTile( IPlugin plugin )
        {
            var wpPlugin = (IWindowsPhonePlugin) plugin;
            var data = new IconicTileData
            {
                Title = wpPlugin.Name,
                IconImage = wpPlugin.Icon,
                SmallIconImage = wpPlugin.SmallIcon
            };

            var uri = new Uri( "/Views/Redirect.xaml?" + PluginKey + "=" + wpPlugin.Id, UriKind.Relative );

            if ( !ShellTile.ActiveTiles.Any( t => t.NavigationUri == uri ) )
            {
                ShellTile.Create( uri, data, false );
            }
        }
    }
}