// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Text.RegularExpressions;
using System.Threading;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.GamerServices;
using PocketCampus.Common;
using PocketCampus.Events.Resources;

namespace PocketCampus.Events.Services
{
    /// <summary>
    /// Prompts the user for their e-mail address.
    /// </summary>
    public sealed class EmailPrompt : IEmailPrompt
    {
        // A very simplified regex for emails.
        private const string EmailRegex = @"^\S+@\S+$";

        /// <summary>
        /// Gets the user's e-mail address.
        /// </summary>
        public string GetEmail()
        {
            ManualResetEvent evt = new ManualResetEvent( false );
            string email = null;

            // HACK to use the built-in keyboard input, it's ugly but much simpler than writing one.
            Guide.BeginShowKeyboardInput( PlayerIndex.One, PluginResources.EmailPromptCaption, PluginResources.EmailPromptMessage, "",
                                          res => { email = Guide.EndShowKeyboardInput( res ); evt.Set(); }, null );

            evt.WaitOne();

            if ( string.IsNullOrWhiteSpace( email ) )
            {
                return null;
            }

            if ( Regex.IsMatch( email, EmailRegex ) )
            {
                return email;
            }

            MessageBoxEx.ShowDialog( PluginResources.InvalidEmailCaption, string.Format( PluginResources.InvalidEmailMessage, email ) );
            return GetEmail();
        }
    }
}