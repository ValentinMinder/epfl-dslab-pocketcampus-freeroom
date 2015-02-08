// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows.Input;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;

namespace PocketCampus.Common.Controls
{
    // BUG: Sometimes tapping the button doesn't change the background color (transitions are triggered though)
    [TemplateVisualState( GroupName = "CommonStates", Name = "Normal" )]
    [TemplateVisualState( GroupName = "CommonStates", Name = "PointerOver" )]
    [TemplateVisualState( GroupName = "CommonStates", Name = "Pressed" )]
    [TemplateVisualState( GroupName = "CommonStates", Name = "Disabled" )]
    public sealed class SimpleButton : ContentControl
    {
        #region Command
        public ICommand Command
        {
            get { return (ICommand) GetValue( CommandProperty ); }
            set { SetValue( CommandProperty, value ); }
        }

        public static readonly DependencyProperty CommandProperty =
            DependencyProperty.Register( "Command", typeof( ICommand ), typeof( SimpleButton ), new PropertyMetadata( null, OnCommandChanged ) );

        private static void OnCommandChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var button = (SimpleButton) obj;

            if ( args.OldValue != null )
            {
                ( (ICommand) args.OldValue ).CanExecuteChanged -= button.Command_CanExecuteChanged;
            }

            if ( args.NewValue != null )
            {
                ( (ICommand) args.NewValue ).CanExecuteChanged += button.Command_CanExecuteChanged;
            }
        }
        #endregion

        #region CommandParameter
        public object CommandParameter
        {
            get { return GetValue( CommandParameterProperty ); }
            set { SetValue( CommandParameterProperty, value ); }
        }

        public static readonly DependencyProperty CommandParameterProperty =
            DependencyProperty.Register( "CommandParameter", typeof( object ), typeof( SimpleButton ), new PropertyMetadata( null ) );
        #endregion

        #region TappedFlyout
        public FlyoutBase TappedFlyout
        {
            get { return (FlyoutBase) GetValue( TappedFlyoutProperty ); }
            set { SetValue( TappedFlyoutProperty, value ); }
        }

        public static readonly DependencyProperty TappedFlyoutProperty =
            DependencyProperty.Register( "TappedFlyout", typeof( FlyoutBase ), typeof( SimpleButton ), new PropertyMetadata( null ) );
        #endregion

        #region HoldingFlyout
        public FlyoutBase HoldingFlyout
        {
            get { return (FlyoutBase) GetValue( HoldingFlyoutProperty ); }
            set { SetValue( HoldingFlyoutProperty, value ); }
        }

        public static readonly DependencyProperty HoldingFlyoutProperty =
            DependencyProperty.Register( "HoldingFlyout", typeof( FlyoutBase ), typeof( SimpleButton ), new PropertyMetadata( null ) );
        #endregion

        public SimpleButton()
        {
            DefaultStyleKey = typeof( SimpleButton );

            Tapped += ( _, __ ) =>
            {
                if ( IsEnabled )
                {
                    VisualStateManager.GoToState( this, "Pressed", true );

                    if ( Command != null )
                    {
                        Command.Execute( CommandParameter );
                    }

                    if ( TappedFlyout != null )
                    {
                        TappedFlyout.ShowAt( this );
                    }
                }
            };

            PointerEntered += ( _, __ ) => SetState( "PointerOver" );
            PointerReleased += ( _, __ ) => SetState( "Normal" );
            PointerCanceled += ( _, __ ) => SetState( "Normal" );
            PointerCaptureLost += ( _, __ ) => SetState( "Normal" );

            Holding += ( _, __ ) =>
            {
                if ( IsEnabled && HoldingFlyout != null )
                {
                    HoldingFlyout.ShowAt( this );
                }
            };
        }


        private void Command_CanExecuteChanged( object sender, EventArgs e )
        {
            IsEnabled = Command.CanExecute( CommandParameter );

            VisualStateManager.GoToState( this, IsEnabled ? "Normal" : "Disabled", true );
        }

        private void SetState( string stateName )
        {
            if ( IsEnabled )
            {
                VisualStateManager.GoToState( this, stateName, true );
            }
        }
    }
}