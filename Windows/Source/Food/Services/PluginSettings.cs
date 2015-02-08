// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Food.Models;
using ThinMvvm;

namespace PocketCampus.Food.Services
{
    public sealed class PluginSettings : SettingsBase<PluginSettings>, IPluginSettings
    {
        public PriceTarget PriceTarget
        {
            get { return Get<PriceTarget>(); }
            set { Set( value ); }
        }

        public bool IsPriceTargetOverriden
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        public double MaximumBudget
        {
            get { return Get<double>(); }
            set { Set( value ); }
        }

        public MealType[] DisplayedMealTypes
        {
            get { return Get<MealType[]>(); }
            set { Set( value ); }
        }

        public Restaurant[] DisplayedRestaurants
        {
            get { return Get<Restaurant[]>(); }
            set { Set( value ); }
        }


        public PluginSettings( ISettingsStorage settings ) : base( settings ) { }


        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.PriceTarget, () => PriceTarget.Student },
                { x => x.IsPriceTargetOverriden, () => false },
                { x => x.MaximumBudget, () => 50.0 }, // this is too much, but it ensures no meal is hidden by default
                { x => x.DisplayedMealTypes, EnumEx.GetValues<MealType> }
            };
        }
    }
}