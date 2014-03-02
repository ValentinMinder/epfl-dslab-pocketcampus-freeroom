using System.Collections.Generic;
using PocketCampus.Common;

namespace PocketCampus.Events.ViewModels.Design
{
    public sealed class DesignSettingsViewModel
    {
#if DEBUG
        public SearchPeriod[] SearchPeriods
        {
            get { return EnumEx.GetValues<SearchPeriod>(); }
        }

        public IPluginSettings Settings
        {
            get { return new DesignSettings { SearchPeriod = SearchPeriod.OneMonth, SearchInPast = true }; }
        }

        private sealed class DesignSettings : IPluginSettings
        {
            public IList<string> UserTickets { get; set; }
            public SearchPeriod SearchPeriod { get; set; }
            public bool SearchInPast { get; set; }
            public IDictionary<int, string> EventCategories { get; set; }
            public IDictionary<string, string> EventTags { get; set; }
            public IDictionary<long, IList<long>> FavoritesByPool { get; set; }
            public IDictionary<long, IList<int>> ExcludedCategoriesByPool { get; set; }
            public IDictionary<long, IList<string>> ExcludedTagsByPool { get; set; }
        }
#endif
    }
}