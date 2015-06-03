// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPluginSettings

#if DEBUG
using System.ComponentModel;
using PocketCampus.Food.Models;

namespace PocketCampus.Food.Services.Design
{
    public sealed class DesignPluginSettings : IPluginSettings
    {
        public PriceTarget PriceTarget
        {
            get { return PriceTarget.Student; }
            set { }
        }

        public bool IsPriceTargetOverriden { get; set; }

        public double MaximumBudget
        {
            get { return 50.00; }
            set { }
        }

        public MealType[] DisplayedMealTypes
        {
            get { return new[] { MealType.Meat, MealType.Fish, MealType.Fish, MealType.Pasta, MealType.Vegetarian }; }
            set { }
        }

#pragma warning disable 0067 // unused event
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif