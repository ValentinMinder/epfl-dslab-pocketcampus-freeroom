// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;
using Windows.UI.Xaml.Controls.Maps;

namespace PocketCampus.Map
{
    public static class EpflLabelsTileSource
    {
        public static HttpMapTileDataSource GetEpflLabelsTileSource()
        {
            var source = new HttpMapTileDataSource();
            source.UriRequested += ( _, e ) =>
            {
                e.Request.Uri = EpflLabelsSource.GetUri( e.X, e.Y, e.ZoomLevel, 256, 256 );
                System.Diagnostics.Debug.WriteLine( e.Request.Uri );
            };
            return source;
        }
    }
}