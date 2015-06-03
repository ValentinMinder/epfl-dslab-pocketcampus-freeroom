// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class Placeholder : ContentControl
    {
        private static readonly DefaultToVisibilityConverter Converter = new DefaultToVisibilityConverter();

        public object For
        {
            get { return GetValue( ForProperty ); }
            set { SetValue( ForProperty, value ); }
        }

        public static readonly DependencyProperty ForProperty =
            DependencyProperty.Register( "For", typeof( object ), typeof( Placeholder ), new PropertyMetadata( null, OnForChanged ) );

        private static void OnForChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (Placeholder) obj ).Visibility = Converter.Convert( args.NewValue );
        }


        public Placeholder()
        {
            DefaultStyleKey = typeof( Placeholder );
        }
    }
}