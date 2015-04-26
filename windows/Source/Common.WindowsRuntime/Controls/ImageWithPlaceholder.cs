// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Markup;
using Windows.UI.Xaml.Media.Imaging;
using Windows.Web.Http;

namespace PocketCampus.Common.Controls
{
    [ContentProperty( Name = "Placeholder" )]
    public sealed class ImageWithPlaceholder : ContentControl
    {
        private static readonly HttpClient Client = new HttpClient();
        private static readonly TimeSpan Timeout = TimeSpan.FromSeconds( 5 );

        public string Source
        {
            get { return (string) GetValue( SourceProperty ); }
            set { SetValue( SourceProperty, value ); }
        }

        public static readonly DependencyProperty SourceProperty =
            DependencyProperty.Register( "Source", typeof( string ), typeof( ImageWithPlaceholder ), new PropertyMetadata( null, OnSourceChanged ) );

        private static async void OnSourceChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var control = (ImageWithPlaceholder) obj;
            var newSource = (string) args.NewValue;

            control._image.Source = null;
            control.SetupPlaceholder();

            try
            {
                var timeoutSource = new CancellationTokenSource( Timeout );
                var content = await Client.GetBufferAsync( new Uri( newSource, UriKind.Absolute ) ).AsTask( timeoutSource.Token );
                var image = new BitmapImage();
                await image.SetSourceAsync( content.AsStream().AsRandomAccessStream() );
                control._image.Source = image;
                control.Content = control._image;
            }
            catch
            {
                // Do nothing. The placeholder is already there.
            }
        }


        public object Placeholder
        {
            get { return (object) GetValue( PlaceholderProperty ); }
            set { SetValue( PlaceholderProperty, value ); }
        }

        public static readonly DependencyProperty PlaceholderProperty =
            DependencyProperty.Register( "Placeholder", typeof( object ), typeof( ImageWithPlaceholder ), new PropertyMetadata( null ) );


        private readonly Image _image;


        public ImageWithPlaceholder()
        {
            HorizontalContentAlignment = HorizontalAlignment.Stretch;
            VerticalContentAlignment = VerticalAlignment.Stretch;

            _image = new Image();
            SetupPlaceholder();
        }


        private void SetupPlaceholder()
        {
            SetBinding( ImageWithPlaceholder.ContentProperty, new Binding { Source = this, Path = new PropertyPath( "Placeholder" ) } );
        }
    }
}