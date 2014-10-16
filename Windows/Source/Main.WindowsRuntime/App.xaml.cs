using System;
using System.Linq;
using System.Net;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using PocketCampus.Main.ViewModels;
using PocketCampus.Main.Views;
using ThinMvvm;
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
            Container.Bind<IBrowserService, BrowserService>();
            Container.Bind<IEmailService, EmailService>();
            Container.Bind<IPhoneService, PhoneService>();
            Container.Bind<ILocationService, LocationService>();
            Container.Bind<ITileService, TileService>();
            Container.Bind<IDeviceIdentifier, DeviceIdentifier>();
            Container.Bind<IAppRatingService, RatingService>();
            Container.Bind<ICredentialsStorage, CredentialsStorage>();

            // Types dependent on one of the above types
            _serverAccess = Container.Bind<IServerAccess, ServerAccess>();

            // Views from Main
            _navigationService.Bind<AboutViewModel, AboutView>();
            _navigationService.Bind<MainViewModel, MainView>();
            _navigationService.Bind<SettingsViewModel, SettingsView>();


            foreach ( var plugin in _pluginLoader.GetPlugins().Cast<IWindowsRuntimePlugin>() )
            {
                // Common init
                plugin.Initialize( (INavigationService) _navigationService );
                // WinRT init
                plugin.Initialize( _navigationService );
            }


            // SecureRequestHandler depends on the auth plugin, so it must be initialized after it
            // TODO: Try moving this to the auth plugin
            Container.Bind<ISecureRequestHandler, SecureRequestHandler>();

            HardwareButtons.BackPressed += ( _, e ) =>
            {
                e.Handled = true;
                _navigationService.NavigateBack();
            };
        }

        protected override async void Launch( LaunchActivatedEventArgs e )
        {
            // TODO launch from protocol

            bool alreadyInitialized = e.PreviousExecutionState == ApplicationExecutionState.Running ||
                                      e.PreviousExecutionState == ApplicationExecutionState.Suspended;

            if ( alreadyInitialized )
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
    }
}