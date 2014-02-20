// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;

namespace PocketCampus.IsAcademia
{
    /// <summary>
    /// Attached properties to extend ListBox functionality.
    /// </summary>
    public sealed class ListBoxExtensions
    {
        public static bool GetScrollSelectionIntoView( DependencyObject obj )
        {
            return (bool) obj.GetValue( ScrollSelectionIntoViewProperty );
        }

        public static void SetScrollSelectionIntoView( DependencyObject obj, bool value )
        {
            obj.SetValue( ScrollSelectionIntoViewProperty, value );
        }

        /// <summary>
        /// Forces a ListBox to scroll its selected item into view.
        /// </summary>
        public static readonly DependencyProperty ScrollSelectionIntoViewProperty =
            DependencyProperty.RegisterAttached( "ScrollSelectionIntoView", typeof( bool ), typeof( ListBoxExtensions ), new PropertyMetadata( OnScrollSelectionIntoViewPropertyChanged ) );

        private static void OnScrollSelectionIntoViewPropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var box = (ListBox) obj;
            if ( (bool) args.NewValue )
            {
                box.SelectionChanged += ListBox_SelectionChanged;
            }
            else
            {
                box.SelectionChanged -= ListBox_SelectionChanged;
            }
        }

        private static void ListBox_SelectionChanged( object sender, SelectionChangedEventArgs e )
        {
            if ( e.AddedItems.Count > 0 )
            {
                var box = ( (ListBox) sender );
                box.Dispatcher.BeginInvoke( () => box.ScrollIntoView( e.AddedItems[0] ) );
            }
        }
    }
}