// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Authentication;
using PocketCampus.Authentication.ViewModels;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/dashboard" )]
    public sealed class MainViewModel : DataViewModel<ViewPluginRequest>
    {
        private readonly INavigationService _navigationService;
        private readonly IServerAccess _serverAccess;
        private readonly IPluginLoader _pluginLoader;
        private readonly IMainSettings _settings;
        private readonly ITileService _tileCreator;
        private readonly ViewPluginRequest _request;

        private IPlugin[] _plugins;


        /// <summary>
        /// Gets the loaded plugins.
        /// </summary>
        public IPlugin[] Plugins
        {
            get { return _plugins; }
            private set { SetProperty( ref _plugins, value ); }
        }

        /// <summary>
        /// Gets the command executed to view the about page.
        /// </summary>
        [LogId( "OpenAbout" )]
        public Command OpenAboutPageCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<AboutViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to view the settings page.
        /// </summary>
        [LogId( "OpenSettings" )]
        public Command OpenSettingsPageCommand
        {
            get { return this.GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to create a plugin "tile" on the user's home screen.
        /// </summary>
        [LogId( "CreatePluginTile" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> CreatePluginTileCommand
        {
            get { return this.GetCommand<IPlugin>( p => _tileCreator.CreateTile( p ) ); }
        }

        /// <summary>
        /// Gets the command executed to open a plugin.
        /// </summary>
        [LogId( "OpenPlugin" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> OpenPluginCommand
        {
            get { return this.GetCommand<IPlugin>( OpenPlugin ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( INavigationService navigationService, IServerAccess serverAccess,
                              IPluginLoader pluginLoader, IMainSettings settings, ITileService tileCreator,
                              ViewPluginRequest request )
        {
            _navigationService = navigationService;
            _pluginLoader = pluginLoader;
            _serverAccess = serverAccess;
            _settings = settings;
            _tileCreator = tileCreator;

            _request = request;
        }


        /// <summary>
        /// Loads plugins.
        /// </summary>
        protected override async Task RefreshAsync( bool force, CancellationToken token )
        {
            if ( Plugins == null )
            {
                Plugins = _pluginLoader.GetPlugins().Where( p => p.IsVisible ).ToArray();

                if ( _request.PluginName == null )
                {
                    ServerConfiguration config;
                    try
                    {
                        config = await _serverAccess.LoadConfigurationAsync();
                        _settings.Configuration = config;
                    }
                    catch
                    {
                        // something went wrong during the fetch, use the saved config
                    }
                }
                else
                {
                    var plugin = Plugins.FirstOrDefault( p => p.Id.Equals( _request.PluginName, StringComparison.OrdinalIgnoreCase ) );
                    if ( plugin != null )
                    {
                        _navigationService.RemoveCurrentFromBackStack();
                        OpenPlugin( plugin );
                    }
                }

                // Filter the plugins anyway, but let the user go to a plugin 
                // from an outside source even if it's filtered out
                FilterPlugins();
            }
        }

        /// <summary>
        /// Filters plugins to only display the ones that are enabled.
        /// </summary>
        [Conditional( "RELEASE" )]
        private void FilterPlugins()
        {
            Plugins = Plugins.Where( p => _settings.Configuration.EnabledPlugins.Any( id => id.Equals( p.Id, StringComparison.OrdinalIgnoreCase ) ) ).ToArray();
        }

        /// <summary>
        /// Opens a plugin.
        /// </summary>
        private void OpenPlugin( IPlugin plugin )
        {
            if ( !plugin.RequiresAuthentication || _settings.SessionStatus != SessionStatus.NotLoggedIn )
            {
                plugin.NavigateTo( _navigationService );
            }
            else if ( _settings.SessionStatus == SessionStatus.NotLoggedIn )
            {
                var authRequest = new AuthenticationRequest( () => plugin.NavigateTo( _navigationService ) );
                _navigationService.NavigateTo<AuthenticationViewModel, AuthenticationRequest>( authRequest );
            }
        }
    }
}