using System;
using PocketCampus.Map.Models;
using ThinMvvm;
using Windows.Devices.Geolocation;
using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;
using Windows.UI.Xaml.Media.Imaging;

namespace PocketCampus.Map.Controls
{
    public sealed class EpflMapLabels : ContentControl
    {
        public MapControl Map
        {
            get { return (MapControl) GetValue( MapProperty ); }
            set { SetValue( MapProperty, value ); }
        }

        public static readonly DependencyProperty MapProperty =
            DependencyProperty.Register( "Map", typeof( MapControl ), typeof( EpflMapLabels ), new PropertyMetadata( null, ( o, _ ) => ( (EpflMapLabels) o ).StartListeningIfPossible() ) );

        public MapProperties Properties
        {
            get { return (MapProperties) GetValue( PropertiesProperty ); }
            set { SetValue( PropertiesProperty, value ); }
        }

        public static readonly DependencyProperty PropertiesProperty =
            DependencyProperty.Register( "Properties", typeof( MapProperties ), typeof( EpflMapLabels ), new PropertyMetadata( null, ( o, _ ) => ( (EpflMapLabels) o ).StartListeningIfPossible() ) );


        private readonly Image _content;

        public EpflMapLabels()
        {
            Content = _content = new Image();
            IsHitTestVisible = false;
        }

        private void StartListeningIfPossible()
        {
            if ( Map != null && Properties != null )
            {
                Properties.ListenToProperty( x => x.Center, UpdateImage );
                Properties.ListenToProperty( x => x.ZoomLevel, UpdateImage );
                Properties.ListenToProperty( x => x.Floor, UpdateImage );
                UpdateImage();
            }
        }

        private void UpdateImage()
        {
            // There is voluntarily no scale factor for the width/height, otherwise the elements are way too small.
            Geopoint topLeft, bottomRight;
            Map.GetLocationFromOffset( new Point( 0, 0 ), out topLeft );
            Map.GetLocationFromOffset( new Point( Map.ActualWidth, Map.ActualHeight ), out bottomRight );
            var uri = EpflLabelsSource.GetUri(
                topLeft.Position.Longitude, topLeft.Position.Latitude,
                bottomRight.Position.Longitude, bottomRight.Position.Latitude,
                Properties.ZoomLevel, Properties.Floor,
                (int) Math.Ceiling( Map.ActualWidth ), (int) Math.Ceiling( Map.ActualHeight ) );
            System.Diagnostics.Debug.WriteLine( uri.ToString() );
            _content.Source = new BitmapImage( uri );
        }
    }
}