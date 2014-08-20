// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;

// Design data for SettingsViewModel

namespace PocketCampus.Map.ViewModels.Design
{
    public sealed class DesignSettingsViewModel
    {
#if DEBUG
        public IPluginSettings Settings { get { return new DesignPluginSettings(); } }

        private sealed class DesignPluginSettings : ObservableObject, IPluginSettings
        {
            public bool UseGeolocation { get; set; }
        }
#endif
    }
}