// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Windows.Media;
using System.Windows.Navigation;
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
    public partial class App : BaseApp
    {
        public new static App Current
        {
            get { return (App) BaseApp.Current; }
        }

        /// <summary>
        /// Gets the navigation service used by the app.
        /// </summary>
        public IWindowsPhoneNavigationService NavigationService { get; private set; }

        /// <summary>
        /// Gets the URI mapper used by the app.
        /// </summary>
        public PocketCampusUriMapper UriMapper { get; private set; }

        /// <summary>
        /// Creates a new App.
        /// </summary>
        public App()
        {
            InitializeComponent();

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

        /// <summary>
        /// Gets the language and flow direction of the app.
        /// </summary>
        protected override Tuple<string, string> GetLanguageAndFlowDirection()
        {
            return Tuple.Create( AppResources.ResourceLanguage, AppResources.ResourceFlowDirection );
        }

        /// <summary>
        /// Initializes the app, by binding interfaces to concrete types and ViewModels to Views, and also loading plugins.
        /// </summary>
        protected override void Initialize()
        {
            // Basic building blocks
            Container.Bind<IHttpClient, HttpClient>();
            Container.Bind<IApplicationSettings, ApplicationSettings>();
            var pluginLoader = Container.Bind<IPluginLoader, PluginLoader>();

            // Single-purpose services with no dependencies
            Container.Bind<Logger, GoogleAnalyticsLogger>();
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            Container.Bind<ITileCreator, TileCreator>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IRatingService, RatingService>();
            Container.Bind<IDataCache, WindowsPhoneDataCache>();

            // URI mapping
            RootFrame.UriMapper = UriMapper = new PocketCampusUriMapper( pluginLoader.GetPlugins() );
            LauncherEx.RegisterProtocol( PocketCampusUriMapper.PocketCampusProtocol, UriMapper.NavigateToCustomUri );

            // ViewModels from Main
            NavigationService = Container.Bind<INavigationService, WindowsPhoneNavigationService>();
            NavigationService.Bind<MainViewModel>( "/Views/MainView.xaml" );
            NavigationService.Bind<AuthenticationViewModel>( "/Views/AuthenticationView.xaml" );
            NavigationService.Bind<SettingsViewModel>( "/Views/SettingsView.xaml" );
            NavigationService.Bind<AboutViewModel>( "/Views/AboutView.xaml" );

            // Common services
            AppInitializer.BindImplementations();

            // Common part of plugin initialization
            AppInitializer.InitializePlugins( pluginLoader, NavigationService );

            // WP-specific part of plugin initialization
            foreach ( var plugin in pluginLoader.GetPlugins().Cast<IWindowsPhonePlugin>() )
            {
                plugin.Initialize( NavigationService );
            }
        }

        /// <summary>
        /// Called when the app runs for the very first time after installation.
        /// </summary>
        protected override void OnFirstRun()
        {
            MessageBoxEx.ShowDialog( AppResources.FirstRunCaption, AppResources.FirstRunMessage );
        }
    }
}