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
                Width = 100,
                Height = 100,
                Child = path
            };
            container.SetBinding( Border.BackgroundProperty, new Binding { Source = icon, Path = new PropertyPath( "Background" ) } );

            icon.HorizontalAlignment = HorizontalAlignment.Center;
            icon.VerticalAlignment = VerticalAlignment.Center;
            icon.Content = new Viewbox
            {
                Child = container
            };
        }
    }
}