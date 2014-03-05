// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Diagnostics;
using System.Linq;
using System.Windows;
using System.Windows.Markup;
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
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

// TODO: This is a mess, clean it up.
// Maybe have a BaseApp class with the common function and a virtual method for init?

namespace PocketCampus.Main
{
    /// <summary>
    /// The PocketCampus application.
    /// </summary>
    public partial class App : Application
    {
        private IPluginLoader _pluginLoader;

        /// <summary>
        /// Gets the root frame of the app.
        /// </summary>
        public static PhoneApplicationFrame RootFrame { get; private set; }

        /// <summary>
        /// Gets the navigation service used by the app.
        /// </summary>
        public static IWindowsPhoneNavigationService NavigationService { get; private set; }

        /// <summary>
        /// Gets the URI mapper used by the app.
        /// </summary>
        public static PocketCampusUriMapper UriMapper { get; private set; }


        /// <summary>
        /// Creates a new App.
        /// </summary>
        public App()
        {
            UnhandledException += Application_UnhandledException;

            RootFrame = new OrientationChangingFrame();

            // Map custom URIs properly
            _pluginLoader = Container.BindOnce<IPluginLoader, PluginLoader>();
            RootFrame.UriMapper = UriMapper = new PocketCampusUriMapper( _pluginLoader.GetPlugins() );

            InitializeComponent();
            InitializePhoneApplication();
        }


        /// <summary>
        /// Binds all dependencies and views.
        /// </summary>
        private void InitializeApplication()
        {
            // Basic building blocks
            Container.Bind<IHttpClient, HttpClient>();
            Container.Bind<IApplicationSettings, ApplicationSettings>();

            // Single-purpose services with no dependencies
            Container.Bind<NavigationLogger, GoogleAnalyticsNavigationLogger>();
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            Container.Bind<ITileCreator, TileCreator>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IRatingService, RatingService>();

            // Common services
            AppInitializer.BindImplementations();

            App.NavigationService = Container.BindOnce<INavigationService, FrameNavigationService>();
            App.NavigationService.Bind<MainViewModel>( "/Views/MainView.xaml" );
            App.NavigationService.Bind<AuthenticationViewModel>( "/Views/AuthenticationView.xaml" );
            App.NavigationService.Bind<SettingsViewModel>( "/Views/SettingsView.xaml" );
            App.NavigationService.Bind<AboutViewModel>( "/Views/AboutView.xaml" );

            // Common part of plugin initialization
            AppInitializer.InitializePlugins( _pluginLoader, App.NavigationService );

            // WP-specific part of plugin initialization
            InitializeWindowsPhonePlugins( _pluginLoader, App.NavigationService );
        }

        private void InitializeWindowsPhonePlugins( IPluginLoader pluginLoader, IWindowsPhoneNavigationService navigationService )
        {
            foreach ( var plugin in pluginLoader.GetPlugins().Cast<IWindowsPhonePlugin>() )
            {
                plugin.Initialize( navigationService );
            }
        }

        private void InitializePhoneApplication()
        {
            RootFrame.Navigated += RootFrame_Navigated;
            RootFrame.NavigationFailed += RootFrame_NavigationFailed;

            // Language display initialization
            InitializeLanguage();

            // Theme initialization
            ThemeManager.OverrideOptions = ThemeManagerOverrideOptions.None;
            ThemeManager.ToLightTheme();
            ThemeManager.SetAccentColor( (Color) Resources["AppAccentColor"] );

            // Debug switches
            if ( Debugger.IsAttached )
            {
                // Display the current frame rate counters?
                Application.Current.Host.Settings.EnableFrameRateCounter = false;

                // Show the areas of the app that are being redrawn in each frame?
                Application.Current.Host.Settings.EnableRedrawRegions = false;

                // Displays the areas of a page that are handed off to GPU with a colored overlay?
                Application.Current.Host.Settings.EnableCacheVisualization = false;

                // Prevent the screen from turning off while under the debugger.
                PhoneApplicationService.Current.UserIdleDetectionMode = IdleDetectionMode.Disabled;
            }
        }

        /// <summary>
        /// Sets the root frame's language and flow direction from the resources.
        /// </summary>
        private void InitializeLanguage()
        {
            try
            {
                RootFrame.Language = XmlLanguage.GetLanguage( AppResources.ResourceLanguage );
                RootFrame.FlowDirection = (FlowDirection) Enum.Parse( typeof( FlowDirection ), AppResources.ResourceFlowDirection );
            }
            catch
            {
                if ( Debugger.IsAttached )
                {
                    Debugger.Break();
                }

                throw;
            }
        }

        /// <summary>
        /// Occurs when a navigation fails.
        /// </summary>
        private void RootFrame_NavigationFailed( object sender, NavigationFailedEventArgs e )
        {
            if ( Debugger.IsAttached )
            {
                Debugger.Break();
            }
        }

        /// <summary>
        /// Occurs after the first navigation.
        /// </summary>
        private void RootFrame_Navigated( object sender, NavigationEventArgs e )
        {
            if ( RootVisual != RootFrame )
            {
                // Set the root visual to allow the application to render
                RootVisual = RootFrame;
                // Initialize the app
                InitializeApplication();
            }

            // TODO find a way to avoid that
            var settings = (IMainSettings) Container.Get( typeof( IMainSettings ), null );

            // Displays the first-run popup if needed
            if ( settings.IsFirstRun )
            {
                MessageBoxEx.ShowDialog( AppResources.FirstRunCaption, AppResources.FirstRunMessage );
                settings.IsFirstRun = false;
            }

            RootFrame.Navigated -= RootFrame_Navigated;
        }

        /// <summary>
        /// Occurs when an unhandled exception is raised.
        /// </summary>
        private void Application_UnhandledException( object sender, ApplicationUnhandledExceptionEventArgs e )
        {
            if ( Debugger.IsAttached )
            {
                Debugger.Break();
            }
        }
    }
}