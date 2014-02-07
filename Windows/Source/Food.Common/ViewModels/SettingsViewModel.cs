// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The ViewModel for changing settings.
    /// </summary>
    [PageLogId( "/food/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        /// <summary>
        /// Gets the plugin settings.
        /// </summary>
        public IPluginSettings Settings { get; private set; }

        /// <summary>
        /// Gets the displayed meal types.
        /// </summary>
        public Pair<MealTypes, bool>[] DisplayedMealTypes { get; private set; }

        /// <summary>
        /// Gets the available price target.
        /// </summary>
        public PriceTarget[] PriceTargets
        {
            get { return new[] { PriceTarget.Student, PriceTarget.PhDStudent, PriceTarget.Staff, PriceTarget.Visitor }; }
        }


        /// <summary>
        /// Creates a new SettingsViewModel.
        /// </summary>
        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
            DisplayedMealTypes = SettingsUtils.GetEnumPairs( Settings.DisplayedMealTypes, MealTypes.Unknown );
        }


        /// <summary>
        /// Executed when the user navigates away from this page.
        /// </summary>
        public override void OnNavigatedFrom()
        {
            Settings.DisplayedMealTypes = SettingsUtils.GetEnumList( DisplayedMealTypes );
        }
    }
}