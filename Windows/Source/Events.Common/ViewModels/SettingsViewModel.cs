using PocketCampus.Common;
using PocketCampus.Mvvm;

namespace PocketCampus.Events.ViewModels
{
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