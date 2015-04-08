// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Markup;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Shapes;

namespace PocketCampus.Common.Controls
{
    public sealed class Icon : ContentControl
    {
        #region IconWidth
        public double IconWidth
        {
            get { return (double) GetValue( IconWidthProperty ); }
            set { SetValue( IconWidthProperty, value ); }
        }

        public static readonly DependencyProperty IconWidthProperty =
            DependencyProperty.Register( "IconWidth", typeof( double ), typeof( Icon ), new PropertyMetadata( 100.0 ) );
        #endregion

        #region IconHeight
        public double IconHeight
        {
            get { return (double) GetValue( IconHeightProperty ); }
            set { SetValue( IconHeightProperty, value ); }
        }

        public static readonly DependencyProperty IconHeightProperty =
            DependencyProperty.Register( "IconHeight", typeof( double ), typeof( Icon ), new PropertyMetadata( 100.0 ) );
        #endregion

        #region Data
        public string Data
        {
            get { return (string) GetValue( DataProperty ); }
            set { SetValue( DataProperty, value ); }
        }

        public static readonly DependencyProperty DataProperty =
            DependencyProperty.Register( "Data", typeof( string ), typeof( Icon ), new PropertyMetadata( null, OnPathChanged ) );

        private static void OnPathChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var icon = (Icon) obj;
            var data = (string) args.NewValue;

            if ( data == null )
            {
                return;
            }

            // Copy it since sharing it isn't permitted
            var path = (Path) XamlReader.Load( "<Path xmlns=\"http://schemas.microsoft.com/winfx/2006/xaml/presentation\" Data=\"" + data + "\" />" );

            path.Stretch = Stretch.Uniform;
            path.HorizontalAlignment = HorizontalAlignment.Center;
            path.VerticalAlignment = VerticalAlignment.Center;
            path.SetBinding( Path.FillProperty, new Binding { Source = icon, Path = new PropertyPath( "Foreground" ) } );
            path.SetBinding( Path.MarginProperty, new Binding { Source = icon, Path = new PropertyPath( "Padding" ) } );

            var container = new Border
            {
                Child = path
            };
            container.SetBinding( Border.WidthProperty, new Binding { Source = icon, Path = new PropertyPath( "IconWidth" ) } );
            container.SetBinding( Border.HeightProperty, new Binding { Source = icon, Path = new PropertyPath( "IconHeight" ) } );
            container.SetBinding( Border.BackgroundProperty, new Binding { Source = icon, Path = new PropertyPath( "Background" ) } );

            icon.HorizontalAlignment = HorizontalAlignment.Center;
            icon.VerticalAlignment = VerticalAlignment.Center;
            icon.Content = new Viewbox
            {
                Child = container
            };
        }
        #endregion
    }
}