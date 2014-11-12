#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Map.Services.Design;
using ThinMvvm.Design;

namespace PocketCampus.Map.ViewModels.Design
{
    public sealed class Design
    {
        public MainViewModel Main { get; private set; }
        public SettingsViewModel Settings { get; private set; }

        public Design()
        {
            Main = new MainViewModel( new DesignLocationService(), new DesignNavigationService(), new DesignMapService(), new DesignPluginSettings(), new MapSearchRequest() );
            Settings = new SettingsViewModel( new DesignPluginSettings() );

            Main.OnNavigatedTo();
            Settings.OnNavigatedTo();
        }
    }
}
#else
namespace PocketCampus.Map.ViewModels.Design
{
    public sealed class Design { }
}
#endif