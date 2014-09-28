using System.Windows.Input;
using Windows.System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common
{
    public sealed class TextBoxEx
    {
        #region EnterCommandParameter
        public static object GetEnterCommandParameter( DependencyObject obj )
        {
            return obj.GetValue( EnterCommandParameterProperty );
        }

        public static void SetEnterCommandParameter( DependencyObject obj, object value )
        {
            obj.SetValue( EnterCommandParameterProperty, value );
        }

        public static readonly DependencyProperty EnterCommandParameterProperty =
            DependencyProperty.RegisterAttached( "EnterCommandParameter", typeof( object ), typeof( TextBoxEx ), new PropertyMetadata( null ) );
        #endregion

        #region EnterCommand
        public static ICommand GetEnterCommand( DependencyObject obj )
        {
            return (ICommand) obj.GetValue( EnterCommandProperty );
        }

        public static void SetEnterCommand( DependencyObject obj, ICommand value )
        {
            obj.SetValue( EnterCommandProperty, value );
        }

        public static readonly DependencyProperty EnterCommandProperty =
            DependencyProperty.RegisterAttached( "EnterCommand", typeof( ICommand ), typeof( TextBoxEx ), new PropertyMetadata( null, OnEnterCommandChanged ) );

        private static void OnEnterCommandChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            // N.B.: No support for changing the command.

            var box = (Control) obj; // cast to Control to support PasswordBox as well as TextBox
            var command = (ICommand) args.NewValue;

            box.KeyDown += ( _, e ) =>
            {
                if ( e.Key == VirtualKey.Enter )
                {
                    var param = GetEnterCommandParameter( box );
                    if ( command.CanExecute( param ) )
                    {
                        command.Execute( param );
                        // HACK to dismiss the keyboard
                        box.IsEnabled = false;
                        box.IsEnabled = true;
                    }
                }
            };
        }
        #endregion
    }
}