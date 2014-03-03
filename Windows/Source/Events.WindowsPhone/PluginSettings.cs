// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;

namespace PocketCampus.Events
{
    public sealed class PluginSettings : SettingsBase, IPluginSettings
    {
        public List<string> UserTickets
        {
            get { return Get<List<string>>(); }
            set { Set( value ); }
        }

        public SearchPeriod SearchPeriod
        {
            get { return Get<SearchPeriod>(); }
            set { Set( value ); }
        }

        public bool SearchInPast
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        public Dictionary<int, string> EventCategories
        {
            get { return Get<Dictionary<int, string>>(); }
            set { Set( value ); }
        }

        public Dictionary<string, string> EventTags
        {
            get { return Get<Dictionary<string, string>>(); }
            set { Set( value ); }
        }

        public List<long> FavoriteEventIds
        {
            get { return Get<List<long>>(); }
            set { Set( value ); }
        }

        public Dictionary<long, List<int>> ExcludedCategoriesByPool
        {
            get { return Get<Dictionary<long, List<int>>>(); }
            set { Set( value ); }
        }

        public Dictionary<long, List<string>> ExcludedTagsByPool
        {
            get { return Get<Dictionary<long, List<string>>>(); }
            set { Set( value ); }
        }

        public PluginSettings( IApplicationSettings applicationSettings ) : base( applicationSettings ) { }

        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues<PluginSettings>
            {
                { x => x.UserTickets, () => new List<string>() },
                { x => x.SearchPeriod,() => SearchPeriod.OneMonth },
                { x => x.SearchInPast, () => false },
                { x => x.EventCategories, () => new Dictionary<int, string>() },
                { x => x.EventTags, () => new Dictionary<string, string>() },
                { x => x.FavoriteEventIds, () => new List<long>() },
                { x => x.ExcludedCategoriesByPool, () => new Dictionary<long, List<int>>() },
                { x => x.ExcludedTagsByPool, () => new Dictionary<long, List<string>>() }
            };
        }
    }
}