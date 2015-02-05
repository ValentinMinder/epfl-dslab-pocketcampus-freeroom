// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Threading.Tasks;
using PocketCampus.Authentication;
using PocketCampus.Authentication.Models;
using PocketCampus.Authentication.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    [LogId( "/dashboard/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        private readonly IAuthenticator _authenticator;
        private readonly INavigationService _navigationService;
        private readonly IAuthenticationService _authenticationService;


        public IMainSettings Settings { get; private set; }

        public ICredentialsStorage Credentials { get; private set; }

        public TileColoring[] AvailableTileColorings
        {
            get { return EnumEx.GetValues<TileColoring>(); }
        }

        [LogId( "LogIn" )]
        public Command LogInCommand
        {
            get { return this.GetCommand( () => Messenger.Send( new AuthenticationRequest() ) ); }
        }

        [LogId( "LogOff" )]
        public AsyncCommand LogOutCommand
        {
            get { return this.GetAsyncCommand( LogOutAsync ); }
        }

        [LogId( "DestroySessions" )]
        public AsyncCommand DestroySessionsCommand
        {
            get { return this.GetAsyncCommand( DestroySessionsAsync ); }
        }


        public SettingsViewModel( IMainSettings settings, IAuthenticator authenticator, INavigationService navigationService,
                                  IAuthenticationService authenticationService, ICredentialsStorage credentials, ITileService tileService )
        {
            Settings = settings;
            Credentials = credentials;
            _authenticator = authenticator;
            _navigationService = navigationService;
            _authenticationService = authenticationService;

            Settings.ListenToProperty( x => x.TileColoring, () => tileService.SetTileColoring( Settings.TileColoring ) );
        }


        private async Task LogOutAsync()
        {
            Settings.SessionStatus = SessionStatus.NotLoggedIn;
            Settings.Session = null;
            Settings.Sessions = new Dictionary<string, string>();
            Credentials.DeleteCredentials();
            await _authenticator.LogOffAsync();
        }

        private async Task DestroySessionsAsync()
        {
            var request = new LogoutRequest { Session = Settings.Session };
            await _authenticationService.DestroyAllSessionsAsync( request );

            await LogOutAsync();
        }
    }
}