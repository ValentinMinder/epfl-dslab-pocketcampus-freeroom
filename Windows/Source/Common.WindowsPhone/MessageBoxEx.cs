// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Windows;
using Microsoft.Xna.Framework.GamerServices;
using PocketCampus.Common.Resources;

namespace PocketCampus.Common
{
    /// <summary>
    /// Additional message boxes.
    /// </summary>
    public static class MessageBoxEx
    {
        private static readonly string[] YesNoStrings = { CommonResources.Yes, CommonResources.No };

        /// <summary>
        /// Displays a simple dialog with an OK button.
        /// </summary>
        /// <param name="caption">The dialog caption.</param>
        /// <param name="message">The dialog message.</param>
        public static void ShowDialog( string caption, string message )
        {
            MessageBox.Show( message, caption, MessageBoxButton.OK );
        }

        /// <summary>
        /// Prompts the user with a yes-or-no question.
        /// </summary>
        /// <param name="caption">The dialog caption.</param>
        /// <param name="message">The dialog message.</param>
        /// <returns>The user's answer.</returns>
        public static bool ShowPrompt( string caption, string message )
        {
            // HACK: WP's MessageBox.Show only allows Ok/Cancel but we need Yes/No
            //       so we have to use XNA's MessageBox, which allows much more customization
            //       But it's asynchronous, so we need to wait until the callback is called.

            int? result = 1; // index of the button the user taps
            var evt = new ManualResetEvent( false );
            Guide.BeginShowMessageBox( caption, message, YesNoStrings, 1, MessageBoxIcon.None,
                                       o => { result = Guide.EndShowMessageBox( o ); evt.Set(); }, null );
            evt.WaitOne();
            return result == 0;
        }
    }
}