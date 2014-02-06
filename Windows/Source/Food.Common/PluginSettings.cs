// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Food.Models;

namespace PocketCampus.Food
{
    /// <summary>
    /// Concrete, storage-backed implementation of IPluginSettings.
    /// </summary>
    public sealed class PluginSettings : SettingsBase, IPluginSettings
    {
        /// <summary>
        /// Gets or sets the current price target.
        /// Some prices change depending on it (usually menus of the day), some don't.
        /// </summary>
        public PriceTarget PriceTarget
        {
            get { return Get<PriceTarget>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the user's maximum budget.
        /// </summary>
        public double MaximumBudget
        {
            get { return Get<double>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets a list of food type filters set by the user.
        /// </summary>
        public MealTypes[] DisplayedMealTypes
        {
            get { return Get<MealTypes[]>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets a list of restaurant filters set by the user.
        /// </summary>
        public Restaurant[] DisplayedRestaurants
        {
            get { return Get<Restaurant[]>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets the map of meal times to the dates at which the last vote for a meal at that time was cast.
        /// </summary>
        public Dictionary<MealTime, DateTime> LastVotes
        {
            get { return Get<Dictionary<MealTime, DateTime>>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates a new PluginSettings.
        /// </summary>
        public PluginSettings( IApplicationSettings settings ) : base( settings ) { }


        /// <summary>
        /// Sets the default values for all settings.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues<PluginSettings>
            {
                { x => x.PriceTarget, () => PriceTarget.Student },
                { x => x.MaximumBudget, () => 50.0 }, // this is too much, but it ensures no dish is hidden by default
                { x => x.DisplayedMealTypes, () => EnumEx.GetValues<MealTypes>() },
                { x => x.LastVotes, () => new Dictionary<MealTime, DateTime> { { MealTime.Lunch, DateTime.MinValue }, { MealTime.Dinner, DateTime.MinValue } } }
            };
        }
    }
}