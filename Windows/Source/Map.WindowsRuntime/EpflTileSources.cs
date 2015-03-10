// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;
using Windows.UI.Xaml.Controls.Maps;

namespace PocketCampus.Map
{
    public static class EpflTileSources
    {
        public static MapTileDataSource GetForBuildings( MapProperties props )
        {
            var source = new HttpMapTileDataSource();
            source.UriRequested += ( _, e ) =>
            {
                e.Request.Uri = EpflBuildingsSource.GetUri( e.X, e.Y, e.ZoomLevel, props.Floor );
            };
            return source;
        }
    }
}