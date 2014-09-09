// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls.Maps;

namespace PocketCampus.Map
{
    public sealed class EpflBuildingsTileSource : HttpMapTileDataSource
    {
        public int Floor
        {
            get { return (int) GetValue( FloorProperty ); }
            set { SetValue( FloorProperty, value ); }
        }

        public static readonly DependencyProperty FloorProperty =
            DependencyProperty.Register( "Floor", typeof( int ), typeof( EpflBuildingsTileSource ), new PropertyMetadata( 0 ) );


        public EpflBuildingsTileSource()
        {
            UriRequested += ( _, e ) =>
            {
                e.Request.Uri = EpflBuildingsSource.GetUri( Floor, e.X, e.Y, e.ZoomLevel );
            };
        }
    }
}