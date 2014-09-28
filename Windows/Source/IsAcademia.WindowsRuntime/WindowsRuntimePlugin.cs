using System;
using PocketCampus.Common;
using PocketCampus.IsAcademia.ViewModels;
using PocketCampus.IsAcademia.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.IsAcademia
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.IsAcademia.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon
        {
            get
            {
                return (string) new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.IsAcademia.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["PluginIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}