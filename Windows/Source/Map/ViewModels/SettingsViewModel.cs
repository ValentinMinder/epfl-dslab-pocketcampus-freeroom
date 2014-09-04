// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    /// <summary>
    /// The ViewModel for settings.
    /// </summary>
    [LogId( "/map/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
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