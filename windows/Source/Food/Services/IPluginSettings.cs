// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using PocketCampus.Food.Models;

namespace PocketCampus.Food.Services
{
    public interface IPluginSettings : INotifyPropertyChanged
    {
        PriceTarget PriceTarget { get; set; }

        // True if the user overrode the price target given by the server
        bool IsPriceTargetOverriden { get; set; }

        double MaximumBudget { get; set; }

        MealType[] DisplayedMealTypes { get; set; }
    }
}