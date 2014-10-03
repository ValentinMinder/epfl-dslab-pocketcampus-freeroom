// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using PocketCampus.Food.Models;

namespace PocketCampus.Food.Services
{
    /// <summary>
    /// Settings for the food plugin.
    /// </summary>
    public interface IPluginSettings : INotifyPropertyChanged
    {
        /// <summary>
        /// Gets or sets the current price target.
        /// Some prices change depending on it (usually menus of the day), some don't.
        /// </summary>
        PriceTarget PriceTarget { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether the user overrode the default price target,
        /// which is given by the server if the user is logged in.
        /// </summary>
        bool IsPriceTargetOverriden { get; set; }

        /// <summary>
        /// Gets or sets the user's maximum budget.
        /// </summary>
        double MaximumBudget { get; set; }

        /// <summary>
        /// Gets or sets the food type filters set by the user.
        /// </summary>
        MealType[] DisplayedMealTypes { get; set; }
    }
}