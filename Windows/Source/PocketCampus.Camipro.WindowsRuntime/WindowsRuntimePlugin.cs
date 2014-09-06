using System;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Camipro.Views;
using PocketCampus.Common;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Camipro
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Camipro.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public object Icon
        {
            get
            {
                return new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Camipro.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["CamiproIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}