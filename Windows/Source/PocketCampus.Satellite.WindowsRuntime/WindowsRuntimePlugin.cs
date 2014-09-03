using PocketCampus.Common;
using PocketCampus.Satellite.ViewModels;
using PocketCampus.Satellite.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;

namespace PocketCampus.Satellite
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Satellite.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}