// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xaml.Interactivity;
using PocketCampus.Map.Models;
using PocketCampus.Map.ViewModels;
using ThinMvvm;
using Windows.ApplicationModel;
using Windows.Devices.Geolocation;
using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.Map
{
    // HACK: The way children are set/accessed is ugly, slow and brittle
    // HACK: The way the ViewModel is accessed is just as ugly and brittle
    // TODO: Apply some basic SwEng :) split it into multiple parts, avoid hacks, etc.
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
        private MapProperties _properties;
        private Image _labelsOverlay;


        public DependencyObject AssociatedObject { get; private set; }

        public void Attach( DependencyObject associatedObject )
        {
            if ( DesignMode.DesignModeEnabled )
            {
                return;
            }

            AssociatedObject = associatedObject;
            _map = (MapControl) associatedObject;
            _labelsOverlay = new Image();
            _labelsOverlay.ImageOpened += ( _, __ ) =>
            {
                Geopoint topLeft;
                _map.GetLocationFromOffset( new Point( 0, 0 ), out topLeft );
                MapControl.SetLocation( _labelsOverlay, topLeft );
                _labelsOverlay.Visibility = Visibility.Visible;
            };
            _map.Children.Add( _labelsOverlay );

            _map.Loaded += ( _, __ ) =>
            {
                _vm = (MainViewModel) _map.DataContext;

                OnFloorChanged( _vm.Properties.Floor );
                OnItemsChanged( _vm.SearchResults );

                _properties = _vm.Properties;

                _vm.Properties.ListenToProperty( x => x.Center, UpdateLabelsOverlay );
                _vm.Properties.ListenToProperty( x => x.Floor, UpdateLabelsOverlay );
                _vm.Properties.ListenToProperty( x => x.ZoomLevel, UpdateLabelsOverlay );
                UpdateLabelsOverlay();

                _vm.Properties.ListenToProperty( x => x.Floor, () => OnFloorChanged( _vm.Properties.Floor ) );
                _vm.ListenToProperty( x => x.SearchResults, () => OnItemsChanged( _vm.SearchResults ) );

                var buildingsDataSource = EpflTileSources.GetForBuildings( _vm.Properties );
                _map.TileSources.Add( new MapTileSource( buildingsDataSource ) );
            };

            // HACK: Force the map to always face North, so that buildings labels are shown properly.
            _map.HeadingChanged += ( _, __ ) => _map.Heading = 0;
        }

        public void Detach()
        {
            throw new NotSupportedException();
        }

        private void UpdateLabelsOverlay()
        {
            _labelsOverlay.Visibility = Visibility.Collapsed;

            // There is voluntarily no scale factor for the width/height, otherwise the elements are way too small.
            Geopoint topLeft, bottomRight;
            _map.GetLocationFromOffset( new Point( 0, 0 ), out topLeft );
            _map.GetLocationFromOffset( new Point( _map.ActualWidth, _map.ActualHeight ), out bottomRight );
            var uri = EpflLabelsSource.GetUri(
                topLeft.Position.Longitude, topLeft.Position.Latitude,
                bottomRight.Position.Longitude, bottomRight.Position.Latitude,
                _properties.ZoomLevel, _properties.Floor,
                (int) Math.Ceiling( _map.ActualWidth ), (int) Math.Ceiling( _map.ActualHeight ) );

            _labelsOverlay.Source = new BitmapImage( uri );
        }


        private void OnFloorChanged( int floor )
        {
            SetItemsVisibility( floor );
            var tileSources = new List<MapTileSource>();
            foreach ( var tileSource in _map.TileSources )
            {
                tileSources.Add( tileSource );
            }
            _map.TileSources.Clear();
            foreach ( var tileSource in tileSources )
            {
                _map.TileSources.Add( tileSource );
            }
        }

        private void OnItemsChanged( MapItem[] items )
        {
            SetMapItems( items );
            SetMapView( items );
            SetMapFloor( items );
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
    }
}