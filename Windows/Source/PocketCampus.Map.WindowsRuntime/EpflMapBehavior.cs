using System;
using System.Linq;
using Microsoft.Xaml.Interactivity;
using PocketCampus.Map.Models;
using PocketCampus.Map.ViewModels;
using ThinMvvm;
using Windows.ApplicationModel.Core;
using Windows.Devices.Geolocation;
using Windows.Foundation;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;

namespace PocketCampus.Map
{
    // HACK: The way children are set/accessed is ugly, slow and brittle
    public sealed class EpflMapBehavior : DependencyObject, IBehavior
    {
        #region ItemTemplate
        public DataTemplate ItemTemplate
        {
            get { return (DataTemplate) GetValue( ItemTemplateProperty ); }
            set { SetValue( ItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty ItemTemplateProperty =
            DependencyProperty.Register( "ItemTemplate", typeof( DataTemplate ), typeof( EpflMapBehavior ), new PropertyMetadata( null ) );
        #endregion

        #region ItemAnchorX
        public double ItemAnchorX
        {
            get { return (double) GetValue( ItemAnchorXProperty ); }
            set { SetValue( ItemAnchorXProperty, value ); }
        }

        public static readonly DependencyProperty ItemAnchorXProperty =
            DependencyProperty.Register( "ItemAnchorX", typeof( double ), typeof( EpflMapBehavior ), new PropertyMetadata( 0.0 ) );
        #endregion

        #region ItemAnchorY
        public double ItemAnchorY
        {
            get { return (double) GetValue( ItemAnchorYProperty ); }
            set { SetValue( ItemAnchorYProperty, value ); }
        }

        public static readonly DependencyProperty ItemAnchorYProperty =
            DependencyProperty.Register( "ItemAnchorY", typeof( double ), typeof( EpflMapBehavior ), new PropertyMetadata( 0.0 ) );
        #endregion

        #region DisabledItemOpacity
        public double DisabledItemOpacity
        {
            get { return (double) GetValue( DisabledItemOpacityProperty ); }
            set { SetValue( DisabledItemOpacityProperty, value ); }
        }

        public static readonly DependencyProperty DisabledItemOpacityProperty =
            DependencyProperty.Register( "DisabledItemOpacity", typeof( double ), typeof( EpflMapBehavior ), new PropertyMetadata( 1.0 ) );
        #endregion


        private MainViewModel _vm;
        private MapControl _map;
        private MapTileSource _previousSource;


        public DependencyObject AssociatedObject { get; private set; }

        public void Attach( DependencyObject associatedObject )
        {
            AssociatedObject = associatedObject;
            _map = (MapControl) associatedObject;

            _map.Loaded += ( _, __ ) =>
            {
                _vm = (MainViewModel) _map.DataContext;

                OnFloorChanged( _vm.Properties.Floor );
                OnItemsChanged( _vm.SearchProvider.SearchResults );

                _vm.Properties.ListenToProperty( x => x.Floor, () => OnFloorChanged( _vm.Properties.Floor ) );
                _vm.SearchProvider.ListenToProperty( x => x.SearchResults, () => OnItemsChanged( _vm.SearchProvider.SearchResults ) );
            };
        }

        public void Detach()
        {
            throw new NotSupportedException();
        }


        private void OnFloorChanged( int floor )
        {
            SetTileSource( floor );
            SetItemsVisibility( floor );
        }

        private void OnItemsChanged( MapItem[] items )
        {
            SetMapItems( items );
            SetMapView( items );
            SetMapFloor( items );
        }


        private void SetTileSource( int floor )
        {
            if ( _previousSource == null )
            {
                // first time
                var zoomedOutDataSource = GetZoomedOutEpflBuildingsDataSource();
                _map.TileSources.Add( new MapTileSource( zoomedOutDataSource, new MapZoomLevelRange { Min = 0, Max = 16 } ) );
            }
            else
            {
                _map.TileSources.Remove( _previousSource );
            }

            var dataSource = GetEpflBuildingsDataSource( floor );
            _previousSource = new MapTileSource( dataSource, new MapZoomLevelRange { Min = 17, Max = 20 } );
            _map.TileSources.Add( _previousSource );
        }

        private void SetItemsVisibility( int floor )
        {
            foreach ( var child in _map.Children.OfType<ContentControl>() )
            {
                child.Opacity = (int) child.Tag == floor ? 1.0 : DisabledItemOpacity;
            }
        }

        private void SetMapItems( MapItem[] items )
        {
            foreach ( var item in _map.Children.OfType<ContentControl>().ToArray() )
            {
                _map.Children.Remove( item );
            }

            if ( items == null )
            {
                return;
            }

            foreach ( var item in items )
            {
                var point = new Geopoint( new BasicGeoposition { Latitude = item.Latitude, Longitude = item.Longitude } );
                var control = new ContentControl
                {
                    ContentTemplate = ItemTemplate,
                    Content = item.Name,
                    Tag = item.Floor ?? 0
                };
                MapControl.SetLocation( control, point );
                MapControl.SetNormalizedAnchorPoint( control, new Point( ItemAnchorX, ItemAnchorY ) );
                _map.Children.Add( control );
            }
        }

        private async void SetMapView( MapItem[] items )
        {
            if ( items == null || items.Length == 0 )
            {
                return;
            }

            var positions = items.Select( i => new BasicGeoposition { Latitude = i.Latitude, Longitude = i.Longitude } );
            var box = GeoboundingBox.TryCompute( positions );
            var margin = new Thickness( 30.0 );
            await _map.TrySetViewBoundsAsync( box, margin, MapAnimationKind.Default );
        }

        private void SetMapFloor( MapItem[] items )
        {
            if ( items == null || items.Length == 0 )
            {
                return;
            }

            _vm.Properties.Floor = ( from i in items group i by i.Floor ?? 0 into g orderby g.Count() descending select g.Key ).First();
            SetItemsVisibility( _vm.Properties.Floor );
        }


        private static MapTileDataSource GetEpflBuildingsDataSource( int? floor )
        {
            var dataSource = new HttpMapTileDataSource();
            dataSource.UriRequested += async ( _, e ) =>
            {
                var def = e.Request.GetDeferral();
                var uri = EpflBuildingsSource.GetUri( e.X, e.Y, e.ZoomLevel, floor );
                await CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync( CoreDispatcherPriority.Normal, () => e.Request.Uri = uri );
                def.Complete();
            };
            return dataSource;
        }

        private static MapTileDataSource GetZoomedOutEpflBuildingsDataSource()
        {
            return GetEpflBuildingsDataSource( null );
        }
    }
}