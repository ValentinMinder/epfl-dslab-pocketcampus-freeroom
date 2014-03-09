﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.ComponentModel;

namespace PocketCampus.Events
{
    /// <summary>
    /// Settings for the Events plugin.
    /// </summary>
    public interface IPluginSettings : INotifyPropertyChanged
    {
        /// <summary>
        /// Gets or sets the stored user tickets.
        /// </summary>
        List<string> UserTickets { get; set; }

        /// <summary>
        /// Gets or sets the search period for events.
        /// </summary>
        SearchPeriod SearchPeriod { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether to search in the past for events.
        /// </summary>
        bool SearchInPast { get; set; }

        /// <summary>
        /// Gets or sets the display names of all event categories.
        /// </summary>
        Dictionary<int, string> EventCategories { get; set; }

        /// <summary>
        /// Gets or sets the display names of all event tags.
        /// </summary>
        Dictionary<string, string> EventTags { get; set; }

        /// <summary>
        /// Gets or sets the IDs of the user's favorite items.
        /// </summary>
        List<long> FavoriteItemIds { get; set; }

        /// <summary>
        /// Gets or sets the excluded category IDs by pool.
        /// </summary>
        Dictionary<long, List<int>> ExcludedCategoriesByPool { get; set; }

        /// <summary>
        /// Gets or sets the excluded tag IDs by pool.
        /// </summary>
        Dictionary<long, List<string>> ExcludedTagsByPool { get; set; }
    }
}