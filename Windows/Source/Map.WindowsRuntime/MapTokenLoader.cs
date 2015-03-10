// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Windows.Storage;
using Windows.UI.Xaml.Controls.Maps;

namespace PocketCampus.Map
{
    public static class MapTokenLoader
    {
        public static async void SetToken( MapControl map, string tokenPath )
        {
            var tokenUri = new Uri( tokenPath, UriKind.Absolute );
            var file = await StorageFile.GetFileFromApplicationUriAsync( tokenUri );
            map.MapServiceToken = await FileIO.ReadTextAsync( file );
        }
    }
}