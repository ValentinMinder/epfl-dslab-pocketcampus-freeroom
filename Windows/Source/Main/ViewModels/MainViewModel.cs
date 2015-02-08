// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Diagnostics;
using System.Linq;
using PocketCampus.Authentication;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    [LogId( "/dashboard" )]
    public sealed class MainViewModel : ViewModel<NoParameter>
    {
        private readonly INavigationService _navigationService;
        private readonly IPluginLoader _pluginLoader;
        private readonly IMainSettings _settings;
        private readonly ITileService _tileService;

        private IPlugin[] _plugins;


        public IPlugin[] Plugins
        {
            get { return _plugins; }
            private set { SetProperty( ref _plugins, value ); }
        }


        [LogId( "OpenAbout" )]
        public Command OpenAboutPageCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<AboutViewModel> ); }
        }

        [LogId( "OpenSettings" )]
        public Command OpenSettingsPageCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        [LogId( "CreatePluginTile" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> CreatePluginTileCommand
        {
            get { return this.GetCommand<IPlugin>( p => _tileService.CreateTile( p, _settings.TileColoring ) ); }
        }

        [LogId( "OpenPlugin" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> OpenPluginCommand
        {
            get { return this.GetCommand<IPlugin>( OpenPlugin ); }
        }


        public MainViewModel( INavigationService navigationService, IPluginLoader pluginLoader, IMainSettings settings, ITileService tileCreator )
        {
            _navigationService = navigationService;
            _pluginLoader = pluginLoader;
            _settings = settings;
            _tileService = tileCreator;
        }


        public override void OnNavigatedTo()
        {
            if ( Plugins == null )
            {
                Plugins = _pluginLoader.GetPlugins().Where( p => p.IsVisible ).ToArray();
                FilterPlugins();
            }
        }


        [Conditional( "RELEASE" )]
        private void FilterPlugins()
        {
            Plugins = Plugins.Where( p => _settings.Configuration.EnabledPlugins.Any( id => id.Equals( p.Id, StringComparison.OrdinalIgnoreCase ) ) ).ToArray();
        }


        private void OpenPlugin( IPlugin plugin )
        {
            OpenPlugin( plugin, _settings, _navigationService );
        }


        // not an ideal place, but we need this both for the app's launch and when opening plugins
        public static void OpenPlugin( IPlugin plugin, IServerSettings settings, INavigationService navigationService )
        {
            if ( !plugin.RequiresAuthentication || settings.SessionStatus != SessionStatus.NotLoggedIn )
            {
                plugin.NavigateTo( navigationService );
            }
            else if ( settings.SessionStatus == SessionStatus.NotLoggedIn )
            {
                Messenger.Send( new AuthenticationRequest( () => plugin.NavigateTo( navigationService ) ) );
            }
        }
    }
}