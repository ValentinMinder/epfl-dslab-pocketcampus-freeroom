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
            get { return (object) GetValue( CommandParameterProperty ); }
            set { SetValue( CommandParameterProperty, value ); }
        }

        public static readonly DependencyProperty CommandParameterProperty =
            DependencyProperty.Register( "CommandParameter", typeof( object ), typeof( SimpleButton ), new PropertyMetadata( null ) );
        #endregion

        #region Flyout
        public FlyoutBase Flyout
        {
            get { return (FlyoutBase) GetValue( FlyoutProperty ); }
            set { SetValue( FlyoutProperty, value ); }
        }

        public static readonly DependencyProperty FlyoutProperty =
            DependencyProperty.Register( "Flyout", typeof( FlyoutBase ), typeof( SimpleButton ), new PropertyMetadata( null ) );
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

                    if ( Flyout != null )
                    {
                        Flyout.ShowAt( this );
                    }
                }
            };

            PointerEntered += ( _, __ ) =>
            {
                if ( IsEnabled )
                {
                    VisualStateManager.GoToState( this, "PointerOver", true );
                }
            };

            PointerExited += ( _, __ ) =>
            {
                if ( IsEnabled )
                {
                    VisualStateManager.GoToState( this, "Normal", true );
                }
            };
        }

        private void Command_CanExecuteChanged( object sender, EventArgs e )
        {
            IsEnabled = Command.CanExecute( CommandParameter );

            if ( IsEnabled )
            {
                VisualStateManager.GoToState( this, "Normal", true );
            }
            else
            {
                VisualStateManager.GoToState( this, "Disabled", true );
            }
        }
    }
}