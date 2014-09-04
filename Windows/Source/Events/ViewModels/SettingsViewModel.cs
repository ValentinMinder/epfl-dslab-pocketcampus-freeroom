// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    /// <summary>
    /// ViewModel for settings.
    /// </summary>
    [LogId( "/events/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        /// <summary>
        /// Gets the available search periods.
        /// </summary>
        public SearchPeriod[] SearchPeriods
        {
            get { return EnumEx.GetValues<SearchPeriod>(); }
        }

        /// <summary>
        /// Gets the settings.
        /// </summary>
        public IPluginSettings Settings { get; private set; }


        /// <summary>
        /// Creates a new SettingsViewModel.
        /// </summary>
        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
        }
    }
}