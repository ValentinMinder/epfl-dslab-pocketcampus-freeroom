// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Food.Models;

// Design data for SettingsViewModel

namespace PocketCampus.Food.ViewModels.Design
{
    public sealed class DesignSettingsViewModel
    {
#if DEBUG
        public PriceTarget[] PriceTargets
        {
            get { return new[] { PriceTarget.Student, PriceTarget.PhDStudent, PriceTarget.Staff, PriceTarget.Visitor }; }
        }

        public Pair<MealTypes, bool>[] DisplayedMealTypes
        {
            get
            {
                return new[]
                {
                        Pair.Create( MealTypes.GreenFork, true ), Pair.Create( MealTypes.Fish, true ),
                        Pair.Create( MealTypes.Meat, true ), Pair.Create( MealTypes.Poultry, true ),
                        Pair.Create( MealTypes.Vegetarian, true ), Pair.Create( MealTypes.Pasta, true ),
                        Pair.Create( MealTypes.Pizza, true ), Pair.Create( MealTypes.Thai, true ),
                        Pair.Create( MealTypes.Indian, true ), Pair.Create( MealTypes.Lebanese, false )
                };
            }
        }

        public IPluginSettings Settings
        {
            get
            {
                return new DesignPluginSettings
                {
                    PriceTarget = PriceTarget.PhDStudent,
                    MaximumBudget = 12.34,
                    DisplayedMealTypes = new[] { MealTypes.Pasta, MealTypes.Pizza, MealTypes.Fish, MealTypes.Indian }
                };
            }
        }

        private sealed class DesignPluginSettings : IPluginSettings
        {
            public PriceTarget PriceTarget { get; set; }
            public double MaximumBudget { get; set; }
            public MealTypes[] DisplayedMealTypes { get; set; }
            public Dictionary<MealTime, DateTime> LastVotes { get; set; }
        }
#endif
    }
}