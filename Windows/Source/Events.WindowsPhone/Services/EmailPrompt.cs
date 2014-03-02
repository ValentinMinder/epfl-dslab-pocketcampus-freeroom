using System.Text.RegularExpressions;
using System.Threading;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.GamerServices;
using PocketCampus.Common;
using PocketCampus.Events.Resources;

namespace PocketCampus.Events.Services
{
    public sealed class EmailPrompt : IEmailPrompt
    {
        private const string EmailRegex = @"^\S+@\S+$"; // very simplified

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

            MessageBoxEx.ShowDialog( PluginResources.InvalidEmailCaption, PluginResources.InvalidEmailMessage );
            return GetEmail();
        }
    }
}