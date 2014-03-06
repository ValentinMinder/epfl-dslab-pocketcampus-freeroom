using System;
using System.Collections;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Media;

namespace PocketCampus.Directory.Controls
{
    public sealed class PaginatingItemsControl : ContentControl
    {
        #region ItemsSource DependencyProperty
        public IEnumerable ItemsSource
        {
            get { return (IEnumerable) GetValue( ItemsSourceProperty ); }
            set { SetValue( ItemsSourceProperty, value ); }
        }

        public static readonly DependencyProperty ItemsSourceProperty =
            DependencyProperty.Register( "ItemsSource", typeof( IEnumerable ), typeof( PaginatingItemsControl ), new PropertyMetadata( null ) );
        #endregion

        #region ItemTemplate DependencyProperty
        public DataTemplate ItemTemplate
        {
            get { return (DataTemplate) GetValue( ItemTemplateProperty ); }
            set { SetValue( ItemTemplateProperty, value ); }
        }

        public static readonly DependencyProperty ItemTemplateProperty =
            DependencyProperty.Register( "ItemTemplate", typeof( DataTemplate ), typeof( PaginatingItemsControl ), new PropertyMetadata( null ) );
        #endregion

        #region ItemRequestCommand DependencyProperty
        public ICommand ItemRequestCommand
        {
            get { return (ICommand) GetValue( ItemRequestCommandProperty ); }
            set { SetValue( ItemRequestCommandProperty, value ); }
        }

        public static readonly DependencyProperty ItemRequestCommandProperty =
            DependencyProperty.Register( "ItemRequestCommand", typeof( ICommand ), typeof( PaginatingItemsControl ), new PropertyMetadata( null ) );
        #endregion

        private const double PreloadingMargin = 15.0;

        private ItemsControl _items;
        private ScrollViewer _scroller;

        public PaginatingItemsControl()
        {
            VerticalAlignment = VerticalAlignment.Stretch;
            VerticalContentAlignment = VerticalAlignment.Stretch;
            HorizontalAlignment = HorizontalAlignment.Stretch;
            HorizontalContentAlignment = HorizontalAlignment.Stretch;

            _items = new ItemsControl();
            _items.SetBinding( ItemsControl.ItemsSourceProperty, new Binding { Path = new PropertyPath( "ItemsSource" ), Source = this } );
            _items.SetBinding( ItemsControl.ItemTemplateProperty, new Binding { Path = new PropertyPath( "ItemTemplate" ), Source = this } );
            _items.Style = (Style) Application.Current.Resources["ScrollingItemsControlStyle"];
            _items.Loaded += Items_Loaded;

            Content = _items;
        }

        private void Items_Loaded( object sender, EventArgs e )
        {
            _scroller = GetChild<ScrollViewer>( _items );

            _scroller.MouseMove += ( _, __ ) =>
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