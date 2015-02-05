// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices.WindowsRuntime;
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

namespace PocketCampus.Main.Services
{
    public sealed class TileService : ITileService
    {
        private const int TilePixelSize = 150;
        private const int TilePadding = 35;
        private const string TileFileSuffix = "_tile.png";


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
        private static readonly Dictionary<TileColoring, string> TilePaths = new Dictionary<TileColoring, string>
        {
            { TileColoring.FullColors, "" },
            { TileColoring.ColorOnTransparent, "/AlternateTiles/ColorOnTransparent" },
            { TileColoring.WhiteOnTransparent, "/AlternateTiles/WhiteOnTransparent" }
        };


        public async void CreateTile( IPlugin plugin )
        {
            var winPlugin = (IWindowsRuntimePlugin) plugin;

            var icon = new Icon
            {
                Data = winPlugin.Icon,
                IconWidth = TilePixelSize,
                IconHeight = TilePixelSize,
                Foreground = new SolidColorBrush( Colors.White ),
                Padding = new Thickness( TilePadding )
            };

            // HACK: To render the icon to a RenderTargetBitmap, the icon needs to be visible, i.e. in the visual tree, and not Collapsed.
            //       But we don't want to show it, so Opacity = 0 and Column/RowSpan = max to make sure it doesn't take up any space
            var tempContainer = new Border { Child = icon, Opacity = 0 };
            Grid.SetColumnSpan( tempContainer, int.MaxValue );
            Grid.SetRowSpan( tempContainer, int.MaxValue );
            var currentRoot = (Grid) ( (Page) ( (Frame) Window.Current.Content ).Content ).Content;
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
            tile.VisualElements.ShowNameOnSquare150x150Logo = true;
            await tile.RequestCreateAsync();
        }

        public void SetTileColoring( TileColoring coloring )
        {
            var xml = new XmlDocument();
            xml.LoadXml( string.Format( TileXmlFormat, TilePaths[coloring] ) );

            var manager = TileUpdateManager.CreateTileUpdaterForApplication();
            manager.EnableNotificationQueue( true );
            manager.Clear();
            manager.AddToSchedule( new ScheduledTileNotification( xml, DateTimeOffset.Now.AddSeconds( 1 ) ) );
        }
    }
}