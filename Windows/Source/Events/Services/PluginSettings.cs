// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Collections.ObjectModel;
using ThinMvvm;

namespace PocketCampus.Events.Services
{
    /// <summary>
    /// Settings for the Events plugin.
    /// </summary>
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        /// <summary>
        /// Gets or sets the stored user tickets.
        /// </summary>
        public string[] UserTickets
        {
            get { return Get<string[]>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the search period for events.
        /// </summary>
        public SearchPeriod SearchPeriod
        {
            get { return Get<SearchPeriod>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets a value indicating whether to search in the past for events.
        /// </summary>
        public bool SearchInPast
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the display names of all event categories.
        /// </summary>
        public Dictionary<int, string> EventCategories
        {
            get { return Get<Dictionary<int, string>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the display names of all event tags.
        /// </summary>
        public Dictionary<string, string> EventTags
        {
            get { return Get<Dictionary<string, string>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the IDs of the user's favorite items.
        /// </summary>
        public ObservableCollection<long> FavoriteItemIds
        {
            get { return Get<ObservableCollection<long>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the excluded category IDs by pool.
        /// </summary>
        public Dictionary<long, int[]> ExcludedCategoriesByPool
        {
            get { return Get<Dictionary<long, int[]>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the excluded tag IDs by pool.
        /// </summary>
        public Dictionary<long, string[]> ExcludedTagsByPool
        {
            get { return Get<Dictionary<long, string[]>>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates a new PluginSettings.
        /// </summary>
        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values of the settings.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.UserTickets, () => new string[0] },
                { x => x.SearchPeriod,() => SearchPeriod.OneMonth },
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