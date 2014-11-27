// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows.Media;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Controls;
using PocketCampus.Main.Resources;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using ThinMvvm;
using ThinMvvm.Logging;
using ThinMvvm.WindowsPhone;

namespace PocketCampus.Main
{
    /// <summary>
    /// The PocketCampus application.
    /// </summary>
    public partial class App : AppBase
    {
        // The key to the redirect parameter for Redirect.xaml.
        public const string RedirectRequestKey = "redirect";
        public const string PocketCampusProtocol = "pocketcampus";

        // Logging stuff
        private const string CustomUriEventId = "OpenPocketCampusURL";
        private const string CustomUriScreenId = "/";

        // Constants for the parsing of PocketCampus URIs.
        private const string PocketCampusPrefix = "pocketcampus://";
        private const string ProtocolPrefix = "/Protocol?encodedLaunchUri=";
        private const char PluginActionDelimiter = '/';
        private const string PluginSuffix = ".plugin.pocketcampus.org";
        private const char ActionParametersDelimiter = '?';
        private const char ParametersSeparator = '&';
        private const char KeyValueDelimiter = '=';

        private readonly IWindowsPhoneNavigationService _navigationService;
        private readonly IPluginLoader _pluginLoader;
        private readonly Logger _logger;
        private readonly IWindowsPhonePlugin[] _plugins;

        protected override string Language
        {
            get { return AppResources.ResourceLanguage; }
        }

        protected override string FlowDirection
        {
            get { return AppResources.ResourceFlowDirection; }
        }

        /// <summary>
        /// Creates a new App.
        /// </summary>
        public App()
        {
            InitializeComponent();

            // Services
            _navigationService = Container.Bind<IWindowsPhoneNavigationService, WindowsPhoneNavigationService>();
            Container.Bind<ISettingsStorage, WindowsPhoneSettingsStorage>();
            Container.Bind<IHttpClient, HttpClient>();
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            Container.Bind<ITileService, TileService>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IRatingService, RatingService>();
            Container.Bind<IDataCache, WindowsPhoneDataCache>();
            Container.Bind<ICredentialsStorage, WindowsPhoneCredentialsStorage>();
            _pluginLoader = Container.Bind<IPluginLoader, PluginLoader>();
            _logger = Container.Bind<Logger, GoogleAnalyticsLogger>();

            // Common part of plugins & services initialization
            AppInitializer.Initialize( _pluginLoader, _navigationService );

            // View-ViewModels bindings for Main
            _navigationService.Bind<MainViewModel>( "/Views/MainView.xaml" );
            _navigationService.Bind<SettingsViewModel>( "/Views/SettingsView.xaml" );
            _navigationService.Bind<AboutViewModel>( "/Views/AboutView.xaml" );

            // URI mapping
            LauncherEx.RegisterProtocol( PocketCampusProtocol, NavigateToCustomUri );

            // WP-specific part of plugin initialization
            _plugins = _pluginLoader.GetPlugins().Cast<IWindowsPhonePlugin>().ToArray();
            foreach ( var plugin in _plugins )
            {
                plugin.Initialize( _navigationService );
            }

            // Debug settings
            DebugSettings.UserIdleDetectionMode = IdleDetectionMode.Disabled;

            // Theme initialization
            ThemeManager.OverrideOptions = ThemeManagerOverrideOptions.ApplicationBarColors;
            ThemeManager.ToLightTheme();
            ThemeManager.SetAccentColor( (Color) Resources["AppAccentColor"] );
        }

        /// <summary>
        /// Creates the root frame of the app.
        /// </summary>
        protected override PhoneApplicationFrame CreateRootFrame()
        {
            return new OrientationChangingFrame();
        }

        protected override void Start( AppArguments arguments )
        {
            // Logging
            _logger.Start();

            // Go to a specific plugin if needed
            string id;
            if ( arguments.NavigationArguments.TryGetValue( TileService.PluginKey, out id ) )
            {
                _navigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest( id ) );
                return;
            }

            // or to a custom URI
            string redirect;
            if ( arguments.NavigationArguments.TryGetValue( RedirectRequestKey, out redirect ) )
            {
                redirect = HttpUtility.UrlDecode( redirect );
                Messenger.Send( new EventLogRequest( CustomUriEventId, redirect, CustomUriScreenId ) );
                NavigateToCustomUri( redirect );
                return;
            }

            // Go to main
            _navigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest() );
        }

        /// <summary>
        /// Navigates to the specified PocketCampus URI.
        /// </summary>
        private void NavigateToCustomUri( string uri )
        {
            var pluginAndParams = ParseQuery( uri );
            _plugins.First( p => p.Id.Equals( pluginAndParams.Item1, StringComparison.OrdinalIgnoreCase ) )
                    .NavigateTo( pluginAndParams.Item2, pluginAndParams.Item3, _navigationService );
        }

        /// <summary>
        /// Parses the specified query to get the plugin name, action name and parameters.
        /// </summary>
        private static Tuple<string, string, Dictionary<string, string>> ParseQuery( string uri )
        {
            // The URI we get from WP has already been URL-decided, but the original URI is URL-encoded too
            string query = HttpUtility.UrlDecode( uri );

            query = query.Replace( PocketCampusPrefix, "" );

            string[] parts = query.Split( PluginActionDelimiter );
            string pluginName = parts[0].Replace( PluginSuffix, "" );

            parts = parts[1].Split( ActionParametersDelimiter );
            string actionName = parts[0];

            if ( parts.Length > 0 )
            {
                var parameters = parts[1].Split( ParametersSeparator ).Select( s => s.Split( KeyValueDelimiter ) ).ToDictionary( s => s[0], s => s[1] );

                return Tuple.Create( pluginName, actionName, parameters );
            }

            return Tuple.Create( pluginName, actionName, new Dictionary<string, string>() );
        }
    }
}