// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Microsoft.Phone.Controls;

namespace PocketCampus.Common
{
    /// <summary>
    /// Attached properties for the TextBox class.
    /// </summary>
    public sealed class TextBoxEx : DependencyObject
    {
        #region EnterCommandProperty
        // The command executed when the user presses the Enter key inside the textbox.

        public static ICommand GetEnterCommand( DependencyObject obj )
        {
            return (ICommand) obj.GetValue( EnterCommandProperty );
        }

        public static void SetEnterCommand( DependencyObject obj, ICommand value )
        {
            obj.SetValue( EnterCommandProperty, value );
        }

        public static readonly DependencyProperty EnterCommandProperty =
            DependencyProperty.RegisterAttached( "EnterCommand", typeof( ICommand ), typeof( TextBoxEx ), new PropertyMetadata( OnEnterCommandChanged ) );

        private static void OnEnterCommandChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var box = obj as Control; // using Control to allow for PasswordBox
            if ( box == null )
            {
                return;
            }

            if ( args.OldValue == null )
            {
                box.KeyUp += TextBox_KeyUp;
            }
            if ( args.NewValue == null )
            {
                box.KeyDown -= TextBox_KeyUp;
            }
        }


        private static async void TextBox_KeyUp( object sender, KeyEventArgs e )
        {
            if ( e.Key == Key.Enter )
            {
                var box = (Control) sender;
                var cmd = GetEnterCommand( box );

                if ( cmd.CanExecute( null ) )
                {
                    e.Handled = true;
                    ( (PhoneApplicationPage) ( (PhoneApplicationFrame) Application.Current.RootVisual ).Content ).Focus();
                    await Task.Delay( 200 );
                    cmd.Execute( null );
                }
            }
        }
        #endregion
    }
}