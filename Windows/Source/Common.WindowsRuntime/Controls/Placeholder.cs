using System;
using System.Collections;
using System.Reflection;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class Placeholder : ContentControl
    {
        public object For
        {
            get { return GetValue( ForProperty ); }
            set { SetValue( ForProperty, value ); }
        }

        public static readonly DependencyProperty ForProperty =
            DependencyProperty.Register( "For", typeof( object ), typeof( Placeholder ), new PropertyMetadata( null, OnForChanged ) );

        private static void OnForChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (Placeholder) obj ).Visibility = IsDefault( args.NewValue ) ? Visibility.Visible : Visibility.Collapsed;
        }


        public Placeholder()
        {
            DefaultStyleKey = typeof( Placeholder );
        }


        private static bool IsDefault( object obj )
        {
            if ( obj == null )
            {
                return true;
            }

            var collection = obj as IEnumerable;
            if ( collection != null )
            {
                return !collection.GetEnumerator().MoveNext();
            }

            if ( obj.GetType().GetTypeInfo().IsValueType )
            {
                return obj == Activator.CreateInstance( obj.GetType() );
            }

            return false;
        }
    }
}