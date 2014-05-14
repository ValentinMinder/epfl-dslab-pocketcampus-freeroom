// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;

namespace PocketCampus.Events
{
    /// <summary>
    /// Settings for the Events plugin.
    /// </summary>
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        /// <summary>
        /// Gets or sets the stored user tickets.
        /// </summary>
        public List<string> UserTickets
        {
            get { return Get<List<string>>(); }
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
        public List<long> FavoriteItemIds
        {
            get { return Get<List<long>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the excluded category IDs by pool.
        /// </summary>
        public Dictionary<long, List<int>> ExcludedCategoriesByPool
        {
            get { return Get<Dictionary<long, List<int>>>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the excluded tag IDs by pool.
        /// </summary>
        public Dictionary<long, List<string>> ExcludedTagsByPool
        {
            get { return Get<Dictionary<long, List<string>>>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates a new PluginSettings.
        /// </summary>
        public PluginSettings( IApplicationSettings applicationSettings ) : base( applicationSettings ) { }


        /// <summary>
        /// Gets the default values of the settings.
        /// </summary>
        protected override SettingsDefaultValues<PluginSettings> GetDefaultValues()
        {
            return new SettingsDefaultValues<PluginSettings>
            {
                { x => x.UserTickets, () => new List<string>() },
                { x => x.SearchPeriod,() => SearchPeriod.OneMonth },
                { x => x.SearchInPast, () => false },
                { x => x.EventCategories, () => new Dictionary<int, string>() },
                { x => x.EventTags, () => new Dictionary<string, string>() },
                { x => x.FavoriteItemIds, () => new List<long>() },
                { x => x.ExcludedCategoriesByPool, () => new Dictionary<long, List<int>>() },
                { x => x.ExcludedTagsByPool, () => new Dictionary<long, List<string>>() }
            };
        }
    }
}