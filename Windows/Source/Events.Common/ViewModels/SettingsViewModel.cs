using PocketCampus.Common;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    [LogId( "/events/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        public SearchPeriod[] SearchPeriods
        {
            get { return EnumEx.GetValues<SearchPeriod>(); }
        }

        public IPluginSettings Settings { get; private set; }

        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
        }
    }
}