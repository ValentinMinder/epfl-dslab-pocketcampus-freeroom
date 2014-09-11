using System;
using PocketCampus.Common;
using PocketCampus.Satellite.ViewModels;
using PocketCampus.Satellite.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Satellite
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Satellite.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public object Icon
        {
            get
            {
                return new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Satellite.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["SatelliteIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}