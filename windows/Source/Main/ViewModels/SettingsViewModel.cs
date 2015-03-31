// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Authentication;
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
        public Command LogOutCommand
        {
            get { return this.GetCommand( LogOut ); }
        }

        [LogId( "DestroySessions" )]
        public AsyncCommand DestroySessionsCommand
        {
            get { return this.GetAsyncCommand( DestroySessionsAsync ); }
        }


        public SettingsViewModel( IMainSettings settings, IAuthenticator authenticator, INavigationService navigationService,
                                  ICredentialsStorage credentials, ITileService tileService )
        {
            Settings = settings;
            Credentials = credentials;
            _authenticator = authenticator;
            _navigationService = navigationService;

            Settings.ListenToProperty( x => x.TileColoring, () => tileService.SetTileColoring( Settings.TileColoring ) );
        }

        // TODO what if logout fails

        private void LogOut()
        {
            Settings.SessionStatus = SessionStatus.NotLoggedIn;
            Settings.Session = null;
            Credentials.DeleteCredentials();
        }

        private async Task DestroySessionsAsync()
        {
            if ( Settings.Session != null )
            {
                await _authenticator.LogOutAsync( Settings.Session );
            }

            LogOut();
        }
    }
}