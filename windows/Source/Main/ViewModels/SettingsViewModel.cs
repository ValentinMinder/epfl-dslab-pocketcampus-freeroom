// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

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
        public IMainSettings Settings { get; private set; }

        public ICredentialsStorage Credentials { get; private set; }

        public TileColoring[] AvailableTileColorings
        {
            get { return EnumEx.GetValues<TileColoring>(); }
        }

        [LogId( "LogOff" )]
        public Command LogOutCommand
        {
            get { return this.GetCommand( LogOut ); }
        }


        public SettingsViewModel( IMainSettings settings, ICredentialsStorage credentials, ITileService tileService )
        {
            Settings = settings;
            Credentials = credentials;

            Settings.ListenToProperty( x => x.TileColoring, () => tileService.SetTileColoring( Settings.TileColoring ) );
        }

        private void LogOut()
        {
            Settings.SessionStatus = SessionStatus.NotLoggedIn;
            Settings.Session = null;
            Credentials.DeleteCredentials();
        }
    }
}