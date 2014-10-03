// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Threading.Tasks;
using PocketCampus.Authentication;
using PocketCampus.Authentication.Models;
using PocketCampus.Authentication.Services;
using PocketCampus.Authentication.ViewModels;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// The settings ViewModel.
    /// </summary>
    [LogId( "/dashboard/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        private readonly IAuthenticator _authenticator;
        private readonly INavigationService _navigationService;
        private readonly IAuthenticationService _authenticationService;

        /// <summary>
        /// Gets the settings.
        /// </summary>
        public IMainSettings Settings { get; private set; }

        /// <summary>
        /// Gets the credentials.
        /// </summary>
        public ICredentialsStorage Credentials { get; private set; }

        /// <summary>
        /// Gets the command executed to log on.
        /// </summary>
        [LogId( "LogIn" )]
        public Command LogInCommand
        {
            get { return this.GetCommand( () => _navigationService.NavigateTo<AuthenticationViewModel, AuthenticationRequest>( new AuthenticationRequest() ) ); }
        }

        /// <summary>
        /// Gets the command executed to log off.
        /// </summary>
        [LogId( "LogOff" )]
        public AsyncCommand LogOffCommand
        {
            get { return this.GetAsyncCommand( ExecuteLogOffCommand ); }
        }

        [LogId( "DestroySessions" )]
        public AsyncCommand DestroySessionsCommand
        {
            get { return this.GetAsyncCommand( ExecuteDestroySessionsCommand ); }
        }

        /// <summary>
        /// Creates a new SettingsViewModel.
        /// </summary>
        public SettingsViewModel( IMainSettings settings, IAuthenticator authenticator, INavigationService navigationService,
                                  IAuthenticationService authenticationService, ICredentialsStorage credentials, ITileService tileService )
        {
            Settings = settings;
            Credentials = credentials;
            _authenticator = authenticator;
            _navigationService = navigationService;
            _authenticationService = authenticationService;

            Settings.ListenToProperty( x => x.UseColoredTile, () =>
            {
                tileService.SetTileColoring( Settings.UseColoredTile );
            } );
        }


        /// <summary>
        /// Logs off.
        /// </summary>
        private async Task ExecuteLogOffCommand()
        {
            Settings.SessionStatus = SessionStatus.NotLoggedIn;
            Settings.Session = null;
            Settings.Sessions = new Dictionary<string, string>();
            Credentials.UserName = null;
            Credentials.Password = null;
            await _authenticator.LogOffAsync();
        }

        /// <summary>
        /// Destroys all user sessions from the server and logs off.
        /// </summary>
        private async Task ExecuteDestroySessionsCommand()
        {
            var request = new LogoutRequest { Session = Settings.Session };
            await _authenticationService.DestroyAllSessionsAsync( request );

            await ExecuteLogOffCommand();
        }
    }
}