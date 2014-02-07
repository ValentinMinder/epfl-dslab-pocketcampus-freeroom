// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Food.Models;

namespace PocketCampus.Food
{
    /// <summary>
    /// Settings for the food plugin.
    /// </summary>
    public interface IPluginSettings
    {
        /// <summary>
        /// Gets or sets the current price target.
        /// Some prices change depending on it (usually menus of the day), some don't.
        /// </summary>
        PriceTarget PriceTarget { get; set; }

        /// <summary>
        /// Gets or sets the user's maximum budget.
        /// </summary>
        double MaximumBudget { get; set; }

        /// <summary>
        /// Gets or sets the food type filters set by the user.
        /// </summary>
        MealTypes[] DisplayedMealTypes { get; set; }

        /// <summary>
        /// Gets the dates at which the last vote for a meal at that time was cast.
        /// </summary>
        Dictionary<MealTime, DateTime> LastVotes { get; set; }
    }
}