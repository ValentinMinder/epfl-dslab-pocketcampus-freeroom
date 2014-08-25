// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO.IsolatedStorage;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Imaging;
using Microsoft.Phone.Shell;
using PocketCampus.Common;
using PocketCampus.Main.Resources;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Handles Live Tiles on the user's home screen.
    /// </summary>
    public sealed class TileService : ITileService
    {
        // Images for the main tile
        private const string MainTileColoredSmallBackgroundImage = "Assets/Tiles/ColoredSmall.png";
        private const string MainTileColoredBackgroundImage = "Assets/Tiles/ColoredMedium.png";
        private const string MainTileWhiteSmallBackgroundImage = "Assets/Tiles/WhiteSmall.png";
        private const string MainTileWhiteBackgroundImage = "Assets/Tiles/WhiteMedium.png";

        // N.B. if (when?) adding notification counts to these tiles, the sizes become 130x202 and 70x110 respectively (margins will have to change)
        private const double TileIconSize = 202;
        private const double SmallTileIconSize = 110;

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
                IconImage = RenderVector( wpPlugin.IconKey, TileIconSize * PhoneHelper.GetScreenScaleFactor() ),
                SmallIconImage = RenderVector( wpPlugin.IconKey, SmallTileIconSize * PhoneHelper.GetScreenScaleFactor() )
            };

            var uri = new Uri( "/Views/Redirect.xaml?" + PluginKey + "=" + wpPlugin.Id, UriKind.Relative );

            var existingTile = ShellTile.ActiveTiles.FirstOrDefault( t => t.NavigationUri == uri );
            if ( existingTile != null )
            {
                existingTile.Delete();
            }

            ShellTile.Create( uri, data, false );
        }

        /// <summary>
        /// Sets the tile coloring; either colored, or white.
        /// </summary>
        public void SetTileColoring( bool useColor )
        {
            ShellTile.ActiveTiles.First().Update( new FlipTileData
            {
                BackgroundImage = new Uri( useColor ? MainTileColoredBackgroundImage : MainTileWhiteBackgroundImage, UriKind.Relative ),
                SmallBackgroundImage = new Uri( useColor ? MainTileColoredSmallBackgroundImage : MainTileWhiteSmallBackgroundImage, UriKind.Relative ),
                Title = AppResources.ApplicationTitle
            } );
        }

        /// <summary>
        /// Renders the vector icon with the specified resource key, as an image of the specified size, and returns an URI for tile icons.
        /// </summary>
        /// <remarks>
        /// The key is for a ControlTemplate, it's the easiest way to store a path in resources.
        /// </remarks>
        private static Uri RenderVector( string templateKey, double size )
        {
            size = Math.Round( size );
            string fileName = string.Format( "Shared/ShellContent/{0}_{1}.png", templateKey, size );

            var control = new ContentControl();
            control.Template = (ControlTemplate) Application.Current.Resources[templateKey];
            control.Measure( new Size( size, size ) );
            control.Arrange( new Rect( 0, 0, size, size ) );

            var bitmap = new WriteableBitmap( (int) size, (int) size );
            bitmap.Render( control, null );
            bitmap.Invalidate();

            using ( var store = IsolatedStorageFile.GetUserStoreForApplication() )
            {
                if ( store.FileExists( fileName ) )
                {
                    store.DeleteFile( fileName );
                }

                using ( var stream = store.CreateFile( fileName ) )
                {
                    new PngWriter( stream, bitmap ).Write();
                }
            }

            return new Uri( "isostore:/" + fileName, UriKind.Absolute );
        }
    }
}