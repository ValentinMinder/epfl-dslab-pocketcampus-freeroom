// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPluginSettings

#if DEBUG
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Threading;

namespace PocketCampus.Events.Services.Design
{
    public sealed class DesignPluginSettings : IPluginSettings
    {
        public ObservableCollection<string> UserTickets
        {
            get { return new ObservableCollection<string>(); }
            set { }
        }

        public SearchPeriod SearchPeriod
        {
            get { return SearchPeriod.OneMonth; }
            set { }
        }

        public bool SearchInPast { get; set; }

        public Dictionary<int, string> EventCategories
        {
            get
            {
                return new DesignEventsService().GetEventPoolAsync( null, CancellationToken.None ).Result.EventCategories;
            }
            set { }
        }

        public Dictionary<string, string> EventTags
        {
            get
            {
                return new DesignEventsService().GetEventPoolAsync( null, CancellationToken.None ).Result.EventTags;
            }
            set { }
        }

        public ObservableCollection<long> FavoriteItemIds
        {
            get { return new ObservableCollection<long>( new long[] { 1, 2, 3 } ); }
            set { }
        }

        public Dictionary<long, int[]> ExcludedCategoriesByPool
        {
            get { return new Dictionary<long, int[]> { { -1, new int[0] } }; }
            set { }
        }

        public Dictionary<long, string[]> ExcludedTagsByPool
        {
            get { return new Dictionary<long, string[]> { { -1, new string[0] } }; }
            set { }
        }

#pragma warning disable 0067 // unused event
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif