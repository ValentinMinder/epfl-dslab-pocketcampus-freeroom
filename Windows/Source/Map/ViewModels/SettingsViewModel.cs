// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Map.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Map.ViewModels
{
    [LogId( "/map/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        public IPluginSettings Settings { get; private set; }


        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
        }
    }
}