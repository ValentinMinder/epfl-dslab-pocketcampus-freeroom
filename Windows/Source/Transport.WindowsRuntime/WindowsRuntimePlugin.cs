using System;
using PocketCampus.Common;
using PocketCampus.Transport.ViewModels;
using PocketCampus.Transport.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Transport
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Transport.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon { get { return null; } }
        public object OLD_Icon
        {
            get
            {
                return new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Transport.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["TransportIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<AddStationViewModel, AddStationView>();
            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<SettingsViewModel, SettingsView>();
        }
    }
}