// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using PocketCampus.Events.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Events.ViewModels
{
    [LogId( "/events/settings" )]
    public sealed class SettingsViewModel : ViewModel<NoParameter>
    {
        public SearchPeriod[] AvailableSearchPeriods
        {
            get { return EnumEx.GetValues<SearchPeriod>(); }
        }


        public IPluginSettings Settings { get; private set; }


        public SettingsViewModel( IPluginSettings settings )
        {
            Settings = settings;
        }
    }
}