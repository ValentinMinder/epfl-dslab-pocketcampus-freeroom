// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// The main ViewModel.
    /// </summary>
    [LogId( "/dashboard" )]
    public sealed class MainViewModel : DataViewModel<string>
    {
        private readonly INavigationService _navigationService;
        private readonly IServerAccess _serverAccess;
        private readonly IPluginLoader _pluginLoader;
        private readonly IMainSettings _settings;
        private readonly ITileCreator _tileCreator;
        private readonly string _requestedPlugin;

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
            get { return GetCommand( _navigationService.NavigateTo<AboutViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to view the settings page.
        /// </summary>
        [LogId( "OpenSettings" )]
        public Command OpenSettingsPageCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to create a plugin "tile" on the user's home screen.
        /// </summary>
        [LogId( "CreatePluginTile" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> CreatePluginTileCommand
        {
            get { return GetCommand<IPlugin>( p => _tileCreator.CreateTile( p ) ); }
        }

        /// <summary>
        /// Gets the command executed to open a plugin.
        /// </summary>
        [LogId( "OpenPlugin" )]
        [LogParameter( "$Param.Id" )]
        public Command<IPlugin> OpenPluginCommand
        {
            get { return GetCommand<IPlugin>( OpenPlugin ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( INavigationService navigationService, IServerAccess serverAccess,
                              IPluginLoader pluginLoader, IMainSettings settings, ITileCreator tileCreator,
                              string requestedPlugin )
        {
            _navigationService = navigationService;
            _pluginLoader = pluginLoader;
            _serverAccess = serverAccess;
            _settings = settings;
            _tileCreator = tileCreator;

            _requestedPlugin = requestedPlugin;
        }


        /// <summary>
        /// Loads plugins.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( Plugins == null )
            {
                ServerConfiguration config;
                try
                {
                    config = await _serverAccess.LoadConfigurationAsync();
                    _settings.ServerConfiguration = config;
                }
                catch
                {
                    // something went wrong during the fetch, use the saved config
                }

                _serverAccess.CurrentConfiguration = _settings.ServerConfiguration;

                Plugins = _pluginLoader.GetPlugins();
                FilterPlugins();

                if ( _requestedPlugin != "" )
                {
                    var plugin = Plugins.FirstOrDefault( p => p.Id == _requestedPlugin );
                    if ( plugin != null )
                    {
                        _navigationService.PopBackStack();
                        OpenPlugin( plugin );
                    }
                }
            }
        }

        /// <summary>
        /// Filters plugins to only display the ones that are enabled.
        /// </summary>
        [Conditional( "RELEASE" )]
        private void FilterPlugins()
        {
            Plugins = Plugins.Where( p => _settings.ServerConfiguration.EnabledPlugins.Contains( p.Id ) ).ToArray();
        }

        /// <summary>
        /// Opens a plugin.
        /// </summary>
        private void OpenPlugin( IPlugin plugin )
        {
            if ( !plugin.RequiresAuthentication || _settings.IsAuthenticated )
            {
                plugin.NavigateTo( _navigationService );
            }
            else if ( !_settings.IsAuthenticated )
            {
                _navigationService.NavigateToDialog<AuthenticationViewModel, AuthenticationMode>( AuthenticationMode.Dialog );
                plugin.NavigateTo( _navigationService );
            }
        }
    }
}