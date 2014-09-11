using System;
using PocketCampus.Common;
using PocketCampus.Food.ViewModels;
using PocketCampus.Food.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Food
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Food.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public object Icon
        {
            get
            {
                return new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Food.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["FoodIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<RatingViewModel, RatingView>();
            navigationService.Bind<SettingsViewModel, SettingsView>();
        }
    }
}