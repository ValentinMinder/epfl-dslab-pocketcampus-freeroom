// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using Windows.Data.Xml.Dom;
using Windows.Graphics.Display;
using Windows.Graphics.Imaging;
using Windows.Storage;
using Windows.UI;
using Windows.UI.Notifications;
using Windows.UI.StartScreen;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

// N.B.: ForegroundText is currently ignored on Windows Phone.

namespace PocketCampus.Main.Services
{
    public sealed class TileService : ITileService
    {
        private const int TilePixelSize = 150;
        private const int TilePadding = 35;
        private const string TileFileSuffix = "_tile.png";
        private static readonly Dictionary<TileColoring, TileProperties> SecondaryTileProperties = new Dictionary<TileColoring, TileProperties>
        {
            { TileColoring.FullColors, new TileProperties( Colors.White, Color.FromArgb( 0xFF, 0x36, 0x36, 0x36 ), ForegroundText.Dark ) },
            { TileColoring.ColorOnTransparent, new TileProperties( Colors.Transparent, Colors.White, ForegroundText.Light ) },
            { TileColoring.WhiteOnTransparent, new TileProperties( Colors.Transparent, Colors.White, ForegroundText.Light ) },
        };


        private const string TileXmlFormat = @"
<tile>
  <visual version=""2"">
    <binding template=""TileSquare150x150Image"">
      <image id=""1"" src=""ms-appx:///Assets{0}/Logo.png"" />
    </binding>
    <binding template=""TileSquare71x71Image"">
      <image id=""1"" src=""ms-appx:///Assets{0}/Square71x71Logo.png"" />
    </binding>
  </visual>
</tile>";
        private static readonly Dictionary<TileColoring, string> PrimaryTilePaths = new Dictionary<TileColoring, string>
        {
            { TileColoring.FullColors, "" },
            { TileColoring.ColorOnTransparent, "/AlternateTiles/ColorOnTransparent" },
            { TileColoring.WhiteOnTransparent, "/AlternateTiles/WhiteOnTransparent" }
        };

        private readonly IPluginLoader _pluginLoader;

        public TileService( IPluginLoader pluginLoader )
        {
            _pluginLoader = pluginLoader;
        }


        public async void CreateTile( IPlugin plugin, TileColoring coloring )
        {
            var tile = await GetTileAsync( plugin, coloring );
            await tile.RequestCreateAsync();
        }

        public async void SetTileColoring( TileColoring coloring )
        {
            UpdatePrimaryTile( coloring );
            await UpdateSecondaryTilesAsync( coloring );
        }


        private void UpdatePrimaryTile( TileColoring coloring )
        {
            var xml = new XmlDocument();
            xml.LoadXml( string.Format( TileXmlFormat, PrimaryTilePaths[coloring] ) );

            var manager = TileUpdateManager.CreateTileUpdaterForApplication();
            manager.EnableNotificationQueue( true );
            manager.Clear();
            manager.AddToSchedule( new ScheduledTileNotification( xml, DateTimeOffset.Now.AddSeconds( 1 ) ) );
        }

        private async Task UpdateSecondaryTilesAsync( TileColoring coloring )
        {
            var existingTiles = await SecondaryTile.FindAllAsync();
            var tileProps = SecondaryTileProperties[coloring];
            foreach ( var plugin in _pluginLoader.GetPlugins() )
            {
                if ( existingTiles.Any( t => t.TileId == plugin.Id ) )
                {
                    var tile = await GetTileAsync( plugin, coloring );
                    await tile.UpdateAsync();
                }
            }
        }

        private async Task<SecondaryTile> GetTileAsync( IPlugin plugin, TileColoring coloring )
        {
            var winPlugin = (IWindowsRuntimePlugin) plugin;

            var tileProps = SecondaryTileProperties[coloring];

            var icon = new Icon
            {
                Data = winPlugin.Icon,
                IconWidth = TilePixelSize,
                IconHeight = TilePixelSize,
                Foreground = tileProps.Foreground,
                Padding = new Thickness( TilePadding )
            };

            // HACK: To render the icon to a RenderTargetBitmap, the icon needs to be visible, i.e. in the visual tree, and not Collapsed.
            //       But we don't want to show it, so Opacity = 0 and Column/RowSpan = max to make sure it doesn't take up any space
            var tempContainer = new Border { Child = icon, Opacity = 0 };
            Grid.SetColumnSpan( tempContainer, int.MaxValue );
            Grid.SetRowSpan( tempContainer, int.MaxValue );
            var currentRoot = (Panel) ( (Page) ( (Frame) Window.Current.Content ).Content ).Content;
            currentRoot.Children.Add( tempContainer );

            var bitmap = new RenderTargetBitmap();
            await bitmap.RenderAsync( icon, TilePixelSize, TilePixelSize );

            currentRoot.Children.Remove( tempContainer );

            string fileName = plugin.Id + TileFileSuffix;
            var file = await ApplicationData.Current.LocalFolder.CreateFileAsync( fileName, CreationCollisionOption.ReplaceExisting );
            var fileUri = new Uri( "ms-appdata:///local/" + fileName, UriKind.Absolute );

            using ( var stream = await file.OpenAsync( FileAccessMode.ReadWrite ) )
            {
                double currentDpi = DisplayInformation.GetForCurrentView().LogicalDpi;
                var encoder = await BitmapEncoder.CreateAsync( BitmapEncoder.PngEncoderId, stream );
                encoder.SetPixelData( BitmapPixelFormat.Rgba8, BitmapAlphaMode.Straight,
                                      (uint) bitmap.PixelWidth, (uint) bitmap.PixelHeight,
                                      currentDpi, currentDpi,
                                      ( await bitmap.GetPixelsAsync() ).ToArray() );
                await encoder.FlushAsync();
            }

            var tile = new SecondaryTile( plugin.Id, winPlugin.Name, plugin.Id, fileUri, TileSize.Square150x150 );
            tile.VisualElements.BackgroundColor = tileProps.Background;
            tile.VisualElements.ForegroundText = tileProps.ForegroundText;
            tile.VisualElements.ShowNameOnSquare150x150Logo = true;
            return tile;
        }


        private sealed class TileProperties
        {
            public Color Background { get; private set; }
            public Brush Foreground { get; private set; }
            public ForegroundText ForegroundText { get; private set; }

            public TileProperties( Color background, Color foreground, ForegroundText foregroundText )
            {
                Background = background;
                Foreground = new SolidColorBrush( foreground );
                ForegroundText = foregroundText;
            }
        }
    }
}