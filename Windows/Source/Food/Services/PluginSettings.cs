// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Food.Models;
using ThinMvvm;

namespace PocketCampus.Food.Services
{
    /// <summary>
    /// Concrete, storage-backed implementation of IPluginSettings.
    /// </summary>
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
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
        /// Gets or sets a value indicating whether the user overrode the default price target,
        /// which is given by the server if the user is logged in.
        /// </summary>
        public bool IsPriceTargetOverriden
        {
            get { return Get<bool>(); }
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
        public MealType[] DisplayedMealTypes
        {
            get { return Get<MealType[]>(); }
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
        /// Creates a new PluginSettings.
        /// </summary>
        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values for all settings.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.PriceTarget, () => PriceTarget.Student },
                { x => x.IsPriceTargetOverriden, () => false },
                { x => x.MaximumBudget, () => 50.0 }, // this is too much, but it ensures no meal is hidden by default
                { x => x.DisplayedMealTypes, () => EnumEx.GetValues<MealType>() }
            };
        }
    }
}