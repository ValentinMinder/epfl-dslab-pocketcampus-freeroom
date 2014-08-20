// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.ComponentModel;
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

        public Pair<MealType, bool>[] DisplayedMealTypes
        {
            get
            {
                return new[]
                {
                        Pair.Create( MealType.GreenFork, true ), Pair.Create( MealType.Fish, true ),
                        Pair.Create( MealType.Meat, true ), Pair.Create( MealType.Poultry, true ),
                        Pair.Create( MealType.Vegetarian, true ), Pair.Create( MealType.Pasta, true ),
                        Pair.Create( MealType.Pizza, true ), Pair.Create( MealType.Thai, true ),
                        Pair.Create( MealType.Indian, true ), Pair.Create( MealType.Lebanese, false )
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
                    DisplayedMealTypes = new[] { MealType.Pasta, MealType.Pizza, MealType.Fish, MealType.Indian }
                };
            }
        }

        private sealed class DesignPluginSettings : IPluginSettings, INotifyPropertyChanged
        {
            public PriceTarget PriceTarget { get; set; }
            public bool IsPriceTargetOverriden { get; set; }
            public double MaximumBudget { get; set; }
            public MealType[] DisplayedMealTypes { get; set; }
            public Dictionary<MealTime, DateTime> LastVotes { get; set; }

            public event PropertyChangedEventHandler PropertyChanged;
            private void IgnoreAboveEvent()
            {
                PropertyChanged( null, null );
            }
        }
#endif
    }
}