// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;

namespace PocketCampus.Map
{
    /// <summary>
    /// Extensions to map functionality.
    /// </summary>
    public sealed class MapEx : DependencyObject
    {
        private const double FullOpacity = 1.0;
        private const double PartialOpacity = 0.5;

        #region Level AttachedProperty
        // The level of an item on a map.

        public static int GetLevel( DependencyObject obj )
        {
            return (int) obj.GetValue( LevelProperty );
        }

        public static void SetLevel( DependencyObject obj, int value )
        {
            obj.SetValue( LevelProperty, value );
        }

        public static readonly DependencyProperty LevelProperty =
            DependencyProperty.RegisterAttached( "Level", typeof( int ), typeof( MapEx ), new PropertyMetadata( int.MinValue, OnLevelChanged ) );
        #endregion

        #region DisplayedLevel AttachedProperty
        // The level the map of an item is currently displaying

        public static int GetDisplayedLevel( DependencyObject obj )
        {
            return (int) obj.GetValue( DisplayedLevelProperty );
        }

        public static void SetDisplayedLevel( DependencyObject obj, int value )
        {
            obj.SetValue( DisplayedLevelProperty, value );
        }

        public static readonly DependencyProperty DisplayedLevelProperty =
            DependencyProperty.RegisterAttached( "DisplayedLevel", typeof( int ), typeof( MapEx ), new PropertyMetadata( int.MinValue, OnLevelChanged ) );
        #endregion

        /// <summary>
        /// Occurs when an item changes its level, or the map changes its displayed level.
        /// </summary>
        private static void OnLevelChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var control = (Control) obj;
            control.Opacity = GetLevel( control ) == GetDisplayedLevel( control ) ? FullOpacity : PartialOpacity;
        }
    }
}