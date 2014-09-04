using System.Collections.Generic;
using PocketCampus.Authentication.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;

namespace PocketCampus.Authentication
{
    /// <summary>
    /// The authentication plugin.
    /// </summary>
    public class Plugin : IPlugin
    {
        /// <summary>
        /// Gets the plugin's ID.
        /// </summary>
        public string Id
        {
            get { return "Authentication"; }
        }

        /// <summary>
        /// This plugin is not visible in the application's main menu.
        /// </summary>
        public bool IsVisible
        {
            get { return false; }
        }

        /// <summary>
        /// This plugin does not require authentication.
        /// </summary>
        public bool RequiresAuthentication
        {
            get { return false; }
        }

        /// <summary>
        /// Initializes the plugin.
        /// </summary>
        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IAuthenticationService, AuthenticationService>();
            Container.Bind<IAuthenticator, TequilaAuthenticator>();
        }

        /// <summary>
        /// Does nothing; this plugin cannot be navigated to.
        /// </summary>
        public void NavigateTo( INavigationService navigationService ) { }

        /// <summary>
        /// Does nothing; this plugin does not handle navigation from external sources.
        /// </summary>
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
    }
}