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
    [PageLogId( "/dashboard" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly INavigationService _navigationService;
        private readonly IServerConfiguration _configLoader;
        private readonly IPluginLoader _pluginLoader;
        private readonly IMainSettings _settings;
        private readonly ITileCreator _tileCreator;

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
        [CommandLogId( "OpenAbout" )]
        public Command OpenAboutPageCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<AboutViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to view the settings page.
        /// </summary>
        [CommandLogId( "OpenSettings" )]
        public Command OpenSettingsPageCommand
        {
            get { return GetCommand( _navigationService.NavigateTo<SettingsViewModel> ); }
        }

        /// <summary>
        /// Gets the command executed to create a plugin "tile" on the user's home screen.
        /// </summary>
        [CommandLogId( "CreatePluginTile" )]
        public Command<IPlugin> CreatePluginTileCommand
        {
            get { return GetCommand<IPlugin>( p => _tileCreator.CreateTile( p ) ); }
        }

        /// <summary>
        /// Gets the command executed to open a plugin.
        /// </summary>
        [CommandLogId( "OpenPlugin" )]
        public Command<IPlugin> OpenPluginCommand
        {
            get { return GetCommand<IPlugin>( OpenPlugin ); }
        }


        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( INavigationService navigationService, IServerConfiguration configLoader,
                              IPluginLoader pluginLoader, IMainSettings settings, ITileCreator tileCreator )
        {
            _navigationService = navigationService;
            _pluginLoader = pluginLoader;
            _configLoader = configLoader;
            _settings = settings;
            _tileCreator = tileCreator;
        }


        /// <summary>
        /// Loads plugins and authenticates if needed.
        /// </summary>
        protected override async Task RefreshAsync( CancellationToken token, bool force )
        {
            if ( Plugins == null )
            {
                await _configLoader.LoadAsync();

                Plugins = _pluginLoader.GetPlugins();
                FilterPlugins();
            }
        }

        [Conditional( "RELEASE" )]
        private void FilterPlugins()
        {
            Plugins = Plugins.Where( p => _configLoader.EnabledPlugins.Contains( p.Id ) ).ToArray();
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
                _navigationService.NavigateToDialog<AuthenticationViewModel>();
                plugin.NavigateTo( _navigationService );
            }
        }
    }
}