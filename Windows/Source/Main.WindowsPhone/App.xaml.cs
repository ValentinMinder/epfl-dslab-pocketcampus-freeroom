// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
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

            // URI mapping
            //RootFrame.UriMapper = UriMapper = new PocketCampusUriMapper( pluginLoader.GetPlugins() );
            //LauncherEx.RegisterProtocol( PocketCampusUriMapper.PocketCampusProtocol, UriMapper.NavigateToCustomUri );

            /*
            string id;
            if ( NavigationContext.QueryString.TryGetValue( TileCreator.PluginKey, out id ) )
            {
                App.Current.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest( id ) );
                return;
            }

            string redirect;
            if ( NavigationContext.QueryString.TryGetValue( PocketCampusUriMapper.RedirectRequestKey, out redirect ) )
            {
                redirect = HttpUtility.UrlDecode( redirect );
                App.Current.UriMapper.NavigateToCustomUri( redirect );
                return;
            }
            */

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
            foreach ( var plugin in deps.PluginLoader.GetPlugins().Cast<IWindowsPhonePlugin>() )
            {
                plugin.Initialize( deps.NavigationService );
            }

            // Go to main
            deps.NavigationService.NavigateTo<MainViewModel, ViewPluginRequest>( new ViewPluginRequest() );
        }
    }
}