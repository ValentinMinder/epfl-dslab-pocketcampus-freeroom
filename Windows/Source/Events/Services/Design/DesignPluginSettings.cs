// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPluginSettings

#if DEBUG
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;

namespace PocketCampus.Events.Services.Design
{
    public sealed class DesignPluginSettings : IPluginSettings
    {
        public string[] UserTickets { get; set; }

        public SearchPeriod SearchPeriod
        {
            get { return SearchPeriod.OneMonth; }
            set { }
        }

        public bool SearchInPast { get; set; }

        public Dictionary<int, string> EventCategories
        {
            get { return new Dictionary<int, string>(); }
            set { }
        }

        public Dictionary<string, string> EventTags
        {
            get { return new Dictionary<string, string>(); }
            set { }
        }

        public ObservableCollection<long> FavoriteItemIds
        {
            get { return new ObservableCollection<long>( new long[] { 1, 2, 3 } ); }
            set { }
        }

        public Dictionary<long, int[]> ExcludedCategoriesByPool { get; set; }

        public Dictionary<long, string[]> ExcludedTagsByPool { get; set; }

#pragma warning disable 0067 // unused event
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif