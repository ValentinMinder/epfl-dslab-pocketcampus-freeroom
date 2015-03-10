// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Collections.ObjectModel;
using ThinMvvm;

namespace PocketCampus.Events.Services
{
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        public ObservableCollection<string> UserTickets
        {
            get { return Get<ObservableCollection<string>>(); }
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

        public ObservableCollection<long> FavoriteItemIds
        {
            get { return Get<ObservableCollection<long>>(); }
            set { Set( value ); }
        }

        public Dictionary<long, int[]> ExcludedCategoriesByPool
        {
            get { return Get<Dictionary<long, int[]>>(); }
            set { Set( value ); }
        }

        public Dictionary<long, string[]> ExcludedTagsByPool
        {
            get { return Get<Dictionary<long, string[]>>(); }
            set { Set( value ); }
        }


        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.UserTickets, () => new ObservableCollection<string>() },
                { x => x.SearchPeriod,() => SearchPeriod.OneWeek },
                { x => x.SearchInPast, () => false },
                { x => x.EventCategories, () => new Dictionary<int, string>() },
                { x => x.EventTags, () => new Dictionary<string, string>() },
                { x => x.FavoriteItemIds, () => new ObservableCollection<long>() },
                { x => x.ExcludedCategoriesByPool, () => new Dictionary<long, int[]>() },
                { x => x.ExcludedTagsByPool, () => new Dictionary<long, string[]>() }
            };
        }
    }
}