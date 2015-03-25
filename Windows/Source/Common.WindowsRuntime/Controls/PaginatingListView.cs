// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections;
using System.Windows.Input;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Media;

namespace PocketCampus.Common.Controls
{
    public sealed class PaginatingListView : ContentControl
    {
        #region ItemsSource DependencyProperty
        public IEnumerable ItemsSource
        {
            get { return (IEnumerable) GetValue( ItemsSourceProperty ); }
            set { SetValue( ItemsSourceProperty, value ); }
        }

        public static readonly DependencyProperty ItemsSourceProperty =
            DependencyProperty.Register( "ItemsSource", typeof( IEnumerable ), typeof( PaginatingListView ), new PropertyMetadata( null ) );
        #endregion

        #region ItemTemplate DependencyProperty
        public DataTemplate ItemTemplate
        {
            get { return (DataTemplate) GetValue( ItemTemplateProperty ); }
            set { SetValue( ItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty ItemTemplateProperty =
            DependencyProperty.Register( "ItemTemplate", typeof( DataTemplate ), typeof( PaginatingListView ), new PropertyMetadata( null ) );
        #endregion

        #region ItemRequestCommand DependencyProperty
        public ICommand ItemRequestCommand
        {
            get { return (ICommand) GetValue( ItemRequestCommandProperty ); }
            set { SetValue( ItemRequestCommandProperty, value ); }
        }

        public static readonly DependencyProperty ItemRequestCommandProperty =
            DependencyProperty.Register( "ItemRequestCommand", typeof( ICommand ), typeof( PaginatingListView ), new PropertyMetadata( null ) );
        #endregion

        private const double PreloadingMargin = 400.0;

        private readonly ListView _view;
        private ScrollViewer _scroller;

        public PaginatingListView()
        {
            VerticalAlignment = VerticalAlignment.Stretch;
            VerticalContentAlignment = VerticalAlignment.Stretch;
            HorizontalAlignment = HorizontalAlignment.Stretch;
            HorizontalContentAlignment = HorizontalAlignment.Stretch;

            _view = new ListView();
            _view.SetBinding( ListView.ItemsSourceProperty, new Binding { Path = new PropertyPath( "ItemsSource" ), Source = this } );
            _view.SetBinding( ListView.ItemTemplateProperty, new Binding { Path = new PropertyPath( "ItemTemplate" ), Source = this } );
            _view.Loaded += Items_Loaded;

            Content = _view;
        }

        private void Items_Loaded( object sender, RoutedEventArgs e )
        {
            _scroller = GetChild<ScrollViewer>( _view );

            _scroller.ViewChanged += ( _, __ ) =>
            {
                if ( _scroller.ScrollableHeight - _scroller.VerticalOffset < PreloadingMargin )
                {
                    if ( ItemRequestCommand != null && ItemRequestCommand.CanExecute( null ) )
                    {
                        ItemRequestCommand.Execute( null );
                    }
                }
            };
        }

        private static T GetChild<T>( DependencyObject obj )
            where T : DependencyObject
        {
            int childrenCount = VisualTreeHelper.GetChildrenCount( obj );
            for ( int n = 0; n < childrenCount; n++ )
            {
                var child = VisualTreeHelper.GetChild( obj, n );
                if ( child.GetType() == typeof( T ) )
                {
                    return (T) child;
                }

                var nestedChild = GetChild<T>( child );
                if ( nestedChild != null )
                {
                    return nestedChild;
                }
            }

            return null;
        }
    }
}