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

        private IWindowsPhoneNavigationService _navigationService;
        private IWindowsPhonePlugin[] _plugins;

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

            Container.Bind<ISettingsStorage, WindowsPhoneSettingsStorage>();
            Container.Bind<IWindowsPhoneNavigationService, WindowsPhoneNavigationService>();

            Container.Bind<IHttpClient, HttpClient>();
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            Container.Bind<ITileService, TileService>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IRatingService, RatingService>();
            Container.Bind<IDataCache, WindowsPhoneDataCache>();
            Container.Bind<ICredentialsStore, WindowsPhoneCredentialsStore>();
            Container.Bind<IPluginLoader, PluginLoader>();

            Container.Bind<AppDependencies, CustomAppDependencies>();

            // Debug settings
            DebugSettings.EnableFrameRateCounter = false;
            DebugSettings.EnableRedrawRegions = false;
            DebugSettings.EnableCacheVisualization = false;
            DebugSettings.UserIdleDetectionMode = IdleDetectionMode.Disabled;

            // Theme initialization
            ThemeManager.OverrideOptions = ThemeManagerOverrideOptions.None;
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

        protected override void Start( AppDependencies dependencies, AppArguments arguments )
        {
            var deps = (CustomAppDependencies) dependencies;
            _navigationService = deps.NavigationService;

            // URI mapping
            LauncherEx.RegisterProtocol( PocketCampusProtocol, NavigateToCustomUri );

            // ViewModels from Main
            deps.NavigationService.Bind<MainViewModel>( "/Views/MainView.xaml" );
            deps.NavigationService.Bind<AuthenticationViewModel>( "/Views/AuthenticationView.xaml" );
            deps.NavigationService.Bind<SettingsViewModel>( "/Views/SettingsView.xaml" );
            deps.NavigationService.Bind<AboutViewModel>( "/Views/AboutView.xaml" );

            // Logging
            new GoogleAnalyticsLogger( deps.NavigationService ).Start();

            // Common services
            AppInitializer.BindImplementations();

            // Common part of plugin initialization
            AppInitializer.InitializePlugins( deps.PluginLoader, deps.NavigationService );

            // WP-specific part of plugin initialization
            _plugins = deps.PluginLoader.GetPlugins().Cast<IWindowsPhonePlugin>().ToArray();
            foreach ( var plugin in _plugins )
            {
                plugin.Initialize( deps.NavigationService );
            }

            // Go to a specific plugin if needed
            string id;
            if ( arguments.NavigationArguments.TryGetValue( TileService.PluginKey, out id ) )
            {
                deps.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest( id ) );
                return;
            }

            // or to a custom URI
            string redirect;
            if ( arguments.NavigationArguments.TryGetValue( RedirectRequestKey, out redirect ) )
            {
                NavigateToCustomUri( redirect );
                return;
            }

            // Go to main
            deps.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest() );
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
            // The URI we get from Windows Phone is URL-encoded...
            string query = HttpUtility.UrlDecode( uri );
            // ...and the original URI is URL-encoded too
            query = HttpUtility.UrlDecode( query );

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