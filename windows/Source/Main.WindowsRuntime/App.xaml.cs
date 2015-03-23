// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Threading.Tasks;
using PocketCampus.CloudPrint;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Main.Views;
using ThinMvvm;
using ThinMvvm.Logging;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Activation;
using Windows.ApplicationModel.DataTransfer;
using Windows.ApplicationModel.DataTransfer.ShareTarget;
using Windows.Phone.UI.Input;
using Windows.Storage;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;
using Windows.UI.Xaml.Resources;

namespace PocketCampus.Main
{
    public sealed partial class App
    {
        private IWindowsRuntimeNavigationService _navigationService;
        private IServerAccess _serverAccess;
        private IMainSettings _settings;
        private IPluginLoader _pluginLoader;
        private ProtocolHandler _protocolHandler;

        // for compatibility
        private ITileService _tileService;

        public App()
        {
            RequestedTheme = ApplicationTheme.Light;
        }

        private void Initialize()
        {
            CustomXamlResourceLoader.Current = new LocalizingResourceLoader();

            LocalizationHelper.Initialize();

            DataViewModelOptions.AddNetworkExceptionType( typeof( WebException ) );
            DataViewModelOptions.AddNetworkExceptionType( typeof( OperationCanceledException ) );

            // ThinMVVM types
            Container.Bind<ISettingsStorage, WindowsRuntimeSettingsStorage>();
            Container.Bind<IDataCache, WindowsRuntimeDataCache>();
            _navigationService = Container.Bind<IWindowsRuntimeNavigationService, WindowsRuntimeNavigationService>();

            // Basic types
            _pluginLoader = Container.Bind<IPluginLoader, PluginLoader>();
            _settings = Container.Bind<IMainSettings, MainSettings>();
            Container.Bind<IHttpClient, HttpClient>();
            Container.Bind<IHttpHeaders, HttpHeaders>();
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            _tileService = Container.Bind<ITileService, TileService>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IAppRatingService, AppRatingService>();
            Container.Bind<ICredentialsStorage, CredentialsStorage>();

            // Logger
            Container.Bind<Logger, GoogleAnalyticsLogger>().Start();

            // Types dependent on one of the above types
            _serverAccess = Container.Bind<IServerAccess, ServerAccess>();
            _protocolHandler = new ProtocolHandler( _pluginLoader, _navigationService );

            // Views from Main
            _navigationService.Bind<AboutViewModel, AboutView>();
            _navigationService.Bind<MainViewModel, MainView>();
            _navigationService.Bind<SettingsViewModel, SettingsView>();

            // Register pocketcampus:// URIs to avoid going to the system and back when they're used inside of the app
            LauncherEx.RegisterProtocol( ProtocolHandler.PocketCampusProtocol, _protocolHandler.NavigateToCustomUri );

            // Initialize plugins, both their common part and their WinRT part
            foreach ( var plugin in _pluginLoader.GetPlugins().Cast<IWindowsRuntimePlugin>() )
            {
                plugin.Initialize( (INavigationService) _navigationService );
                plugin.Initialize( _navigationService );
            }

            // Handle the back button, since Windows Phone doesn't do it for us any more (unlike WP8 "Silverlight")
            HardwareButtons.BackPressed += ( _, e ) =>
            {
                e.Handled = true;
                _navigationService.NavigateBack();
            };

            // COMPAT: Deal with bugs from previous versions :-(
            HandleCompatibilityIssues();
        }

        protected override Frame CreateRootFrame()
        {
            var frame = base.CreateRootFrame();
            frame.ContentTransitions = new TransitionCollection
            {
                new NavigationThemeTransition
                {
                    DefaultNavigationTransitionInfo = new CommonNavigationTransitionInfo
                    {
                        IsStaggeringEnabled = true
                    }
                }
            };
            return frame;
        }

        protected override async void Launch( LaunchActivatedEventArgs e )
        {
            bool alreadyInitialized = e.PreviousExecutionState == ApplicationExecutionState.Running ||
                                      e.PreviousExecutionState == ApplicationExecutionState.Suspended;

            if ( !alreadyInitialized )
            {
                Initialize();

                // Try to load the config; if it fails, it's not a big deal, there's always a saved one
                try
                {
                    _settings.Configuration = await _serverAccess.LoadConfigurationAsync();
                }
                catch { }
            }

            var tilePlugin = _pluginLoader.GetPlugins().FirstOrDefault( p => p.Id == e.TileId );
            if ( tilePlugin != null )
            {
                MainViewModel.OpenPlugin( tilePlugin, _settings, _navigationService );
            }
            else if ( !alreadyInitialized )
            {
                _navigationService.NavigateTo<MainViewModel>();
            }
        }

        protected override void OnActivated( IActivatedEventArgs args )
        {
            if ( args.Kind != ActivationKind.Protocol )
            {
                return;
            }

            using ( new Initializer( this ) )
            {
                var protArgs = (ProtocolActivatedEventArgs) args;
                _protocolHandler.NavigateToCustomUri( protArgs.Uri );
            }
        }

        protected override async void OnShareTargetActivated( ShareTargetActivatedEventArgs args )
        {
            using ( new Initializer( this ) )
            {
                Messenger.Send( await MakeRequestAsync( args.ShareOperation ) );
            }
        }

        private static async Task<PrintRequest> MakeRequestAsync( ShareOperation operation )
        {
            var formats = operation.Data.AvailableFormats;
            if ( formats.Contains( StandardDataFormats.StorageItems ) )
            {
                var items = await operation.Data.GetStorageItemsAsync();

                // TODO support multiple files.
                // (atomic printing would be nice for groups of files)

                var file = items.OfType<StorageFile>().FirstOrDefault();
                if ( file != null )
                {
                    // Passing the file as an URI is required,
                    // but it can't be converted back to a StorageFile if it's not in our app's folders.
                    var copy = await file.CopyAsync( ApplicationData.Current.TemporaryFolder, file.Name, NameCollisionOption.GenerateUniqueName );
                    return new PrintRequest( file.Name, new Uri( copy.Path, UriKind.Absolute ) );
                }
            }

            // We can't display share errors on WP, so...
            return null;
        }

        private void HandleCompatibilityIssues()
        {
            // TileService had a bug in v2.5.0, re-apply the tile coloring to fix it
            if ( Version.Parse( _settings.LastUsedVersion ) < new Version( 2, 5, 1 ) )
            {
                _tileService.SetTileColoring( _settings.TileColoring );
            }

            _settings.LastUsedVersion = typeof( App ).GetTypeInfo().Assembly.GetName().Version.ToString();
        }

        private sealed class Initializer : IDisposable
        {
            public Initializer( App app )
            {
                RootFrame = app.CreateRootFrame();
                Window.Current.Content = RootFrame;
                app.Initialize();
            }

            public void Dispose()
            {
                Window.Current.Activate();
            }
        }
    }
}