// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class Condition : ContentControl
    {
        #region True
        public object True
        {
            get { return GetValue( TrueProperty ); }
            set { SetValue( TrueProperty, value ); }
        }

        public static readonly DependencyProperty TrueProperty =
            DependencyProperty.Register( "True", typeof( object ), typeof( Condition ), new PropertyMetadata( null ) );
        #endregion

        #region False
        public object False
        {
            get { return GetValue( FalseProperty ); }
            set { SetValue( FalseProperty, value ); }
        }

        public static readonly DependencyProperty FalseProperty =
            DependencyProperty.Register( "False", typeof( object ), typeof( Condition ), new PropertyMetadata( null ) );
        #endregion

        #region Value
        public bool Value
        {
            get { return (bool) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( bool ), typeof( Condition ), new PropertyMetadata( false, OnValueChanged ) );

        private static void OnValueChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (Condition) obj ).Update();
        }
        #endregion


        public Condition()
        {
            DefaultStyleKey = typeof( Condition );

            // Since WinRT doesn't like nullable types in DependencyProperties, we can't use a bool? to always trigger ValueChanged
            Loaded += ( _, __ ) => Update();
        }


        private void Update()
        {
            if ( Value )
            {
                Content = True;
            }
            else
            {
                Content = False;
            }
        }
    }
}