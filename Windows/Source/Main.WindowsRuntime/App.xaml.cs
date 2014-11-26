using System;
using System.Linq;
using System.Net;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Main.Views;
using ThinMvvm;
using ThinMvvm.Logging;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Activation;
using Windows.Phone.UI.Input;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Resources;

namespace PocketCampus.Main
{
    public sealed partial class App
    {
        private readonly IWindowsRuntimeNavigationService _navigationService;
        private readonly IServerAccess _serverAccess;
        private readonly IMainSettings _settings;
        private readonly IPluginLoader _pluginLoader;
        private readonly ProtocolHandler _protocolHandler;

        public App()
        {
            RequestedTheme = ApplicationTheme.Light;

            CustomXamlResourceLoader.Current = new LocalizingResourceLoader();

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
            Container.Bind<ITileService, TileService>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IAppRatingService, RatingService>();
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
        }

        protected override async void Launch( LaunchActivatedEventArgs e )
        {
            bool alreadyInitialized = e.PreviousExecutionState == ApplicationExecutionState.Running ||
                                      e.PreviousExecutionState == ApplicationExecutionState.Suspended;

            if ( !alreadyInitialized )
            {
                // This must be done here, after window.current.content is set
                LocalizationHelper.Initialize();

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
            if ( args.Kind == ActivationKind.Protocol )
            {
                var protArgs = (ProtocolActivatedEventArgs) args;
                _protocolHandler.NavigateToCustomUri( protArgs.Uri );
            }
        }
    }
}