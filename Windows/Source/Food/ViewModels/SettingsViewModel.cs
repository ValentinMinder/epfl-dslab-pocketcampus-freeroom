// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Food.Models;
using PocketCampus.Food.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Food.ViewModels
{
    /// <summary>
    /// The ViewModel for changing settings.
    /// </summary>
    [LogId( "/food/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        private readonly PriceTarget _previousTarget;


        public IPluginSettings Settings { get; private set; }

        public Pair<MealType, bool>[] DisplayedMealTypes { get; private set; }

        public PriceTarget[] PriceTargets
        {
            get { return new[] { PriceTarget.Student, PriceTarget.PhDStudent, PriceTarget.Staff, PriceTarget.Visitor }; }
        }


        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
            DisplayedMealTypes = SettingsUtils.GetEnumPairs( Settings.DisplayedMealTypes, MealType.Unknown );

            _previousTarget = settings.PriceTarget;
        }


        public override void OnNavigatedFrom()
        {
            Settings.DisplayedMealTypes = SettingsUtils.GetEnumList( DisplayedMealTypes );

            if ( Settings.PriceTarget != _previousTarget )
            {
                Settings.IsPriceTargetOverriden = true;
            }
        }
    }
}