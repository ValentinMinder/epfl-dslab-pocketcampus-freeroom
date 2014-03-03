// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;

// Design data for EventPoolViewModel

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
            public List<string> UserTickets { get; set; }
            public SearchPeriod SearchPeriod { get; set; }
            public bool SearchInPast { get; set; }
            public Dictionary<int, string> EventCategories { get; set; }
            public Dictionary<string, string> EventTags { get; set; }
            public List<long> FavoriteEventIds { get; set; }
            public Dictionary<long, List<int>> ExcludedCategoriesByPool { get; set; }
            public Dictionary<long, List<string>> ExcludedTagsByPool { get; set; }
        }
#endif
    }
}