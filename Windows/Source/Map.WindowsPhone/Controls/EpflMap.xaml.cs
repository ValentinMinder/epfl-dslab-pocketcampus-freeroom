// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Device.Location;
using System.Linq;
using System.Reflection;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Microsoft.Phone.Maps;
using Microsoft.Phone.Maps.Controls;
using Microsoft.Phone.Maps.Toolkit;
using PocketCampus.Common;
using PocketCampus.Map.Models;
using PocketCampus.Map.ViewModels;
using ThinMvvm;

// MEGA-HACK: Accessing MapItemsControl map children as members fails. They're always null.
// So, instead we find them amongst all map children. It's ugly, but I haven't found a better way.
// TODO: Find a better way.

namespace PocketCampus.Map.Controls
{
    /// <summary>
    /// Displays a map of EPFL.
    /// </summary>
    public partial class EpflMap : UserControl
    {
        #region Properties DependencyProperty
        /// <summary>
        /// The map properties.
        /// </summary>
        public MapProperties Properties
        {
            get { return (MapProperties) GetValue( PropertiesProperty ); }
            set { SetValue( PropertiesProperty, value ); }
        }

        public static readonly DependencyProperty PropertiesProperty =
            DependencyProperty.Register( "Properties", typeof( MapProperties ), typeof( EpflMap ), new PropertyMetadata( OnPropertiesChanged ) );

        private static void OnPropertiesChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var map = (EpflMap) obj;
            var props = (MapProperties) args.NewValue;
            props.ListenToProperty( x => x.UserPosition, map.UserPositionChanged );
            props.ListenToProperty( x => x.BuildingsLevel, map.BuildingsLevelChanged );
        }
        #endregion

        #region Layers DependencyProperty
        /// <summary>
        /// The map layers.
        /// </summary>
        public PocketCampus.Map.Models.MapLayer[] Layers
        {
            get { return (PocketCampus.Map.Models.MapLayer[]) GetValue( LayersProperty ); }
            set { SetValue( LayersProperty, value ); }
        }

        public static readonly DependencyProperty LayersProperty =
            DependencyProperty.Register( "Layers", typeof( PocketCampus.Map.Models.MapLayer[] ), typeof( EpflMap ), new PropertyMetadata( OnLayersChanged ) );

        private static void OnLayersChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var map = (EpflMap) obj;
            var itemsContainer = MapExtensions.GetChildren( map.LayoutRoot ).OfType<MapItemsControl>().ElementAt( 1 );
            itemsContainer.Items.Clear();
            foreach ( var layer in (PocketCampus.Map.Models.MapLayer[]) args.NewValue )
            {
                foreach ( var item in layer.Items )
                {
                    itemsContainer.Items.Add( item );
                }
            }
        }
        #endregion

        #region PinnedLocations DependencyProperty
        /// <summary>
        /// The locations pinned on the map.
        /// </summary>
        public MapItem[] PinnedLocations
        {
            get { return (MapItem[]) GetValue( PinnedLocationsProperty ); }
            set { SetValue( PinnedLocationsProperty, value ); }
        }

        public static readonly DependencyProperty PinnedLocationsProperty =
            DependencyProperty.Register( "PinnedLocations", typeof( MapItem[] ), typeof( EpflMap ), new PropertyMetadata( OnPinnedLocationsChanged ) );

        private static void OnPinnedLocationsChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (EpflMap) obj ).OnPinnedLocationsChanged();
        }
        #endregion


        /// <summary>
        /// Creates a new EpflMap.
        /// </summary>
        public EpflMap()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;
            LayoutRoot.Loaded += LayoutRoot_Loaded;
        }


        /// <summary>
        /// Occurs when the EpflMap has been loaded.
        /// </summary>
        private void LayoutRoot_Loaded( object sender, RoutedEventArgs e )
        {
            string text = AssemblyReader.GetText( typeof( EpflMap ).GetTypeInfo().Assembly, "PocketCampus.Map.MapTokens.txt" );
            if ( text == string.Empty )
            {
#if DEBUG
                MessageBox.Show( "The file with the map tokens is missing. "
                               + "Please add a file called MapTokens.txt in PocketCampus.Map.WindowsPhone with BuildAction 'Embedded Resource'. "
                               + "The first line contains the application ID, the second like the authentication token." );
#endif
            }
            else
            {
                string[] tokens = text.Split();
                MapsSettings.ApplicationContext.ApplicationId = tokens[0];
                MapsSettings.ApplicationContext.AuthenticationToken = tokens[1];
            }
        }

        /// <summary>
        /// Occurs when the users taps a pushpin on the map.
        /// </summary>
        private void Pushpin_Tap( object sender, GestureEventArgs e )
        {
            var loc = (MapItem) ( (Pushpin) sender ).DataContext;
            Properties.Center = loc.Position;
            Properties.BuildingsLevel = loc.Floor ?? 0;
        }

        /// <summary>
        /// Called when the pinned locations change.
        /// </summary>
        private void OnPinnedLocationsChanged()
        {
            var itemsContainer = MapExtensions.GetChildren( LayoutRoot ).OfType<MapItemsControl>().ElementAt( 0 );
            itemsContainer.Items.Clear();
            foreach ( var loc in PinnedLocations )
            {
                itemsContainer.Items.Add( loc );
            }

            var coords = PinnedLocations.Select( loc => loc.Position )
                                        .Select( p => new GeoCoordinate( p.Latitude, p.Longitude ) )
                                        .ToArray();

            if ( coords.Length == 1 )
            {
                LayoutRoot.Center = coords[0];
                Properties.BuildingsLevel = PinnedLocations[0].Floor ?? 0;
            }
            else if ( coords.Length > 1 )
            {
                LayoutRoot.SetView( LocationRectangle.CreateBoundingRectangle( coords ) );
                Properties.BuildingsLevel = PinnedLocations[0].Floor ?? 0;
            }
        }

        /// <summary>
        /// Called when the user's position changes.
        /// </summary>
        private void UserPositionChanged()
        {
            if ( Properties.UserPosition != null )
            {
                Properties.Center = Properties.UserPosition;
            }
        }

        /// <summary>
        /// Called when the buildings level is changed.
        /// </summary>
        private void BuildingsLevelChanged()
        {
            var source = (EpflBuildingsTileSource) LayoutRoot.TileSources[0];
            source.Level = Properties.BuildingsLevel;
            LayoutRoot.TileSources.Remove( source );
            LayoutRoot.TileSources.Add( source );
        }
    }
}