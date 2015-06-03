// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPluginSettings

#if DEBUG
using System.Collections.ObjectModel;
using PocketCampus.Transport.Models;

namespace PocketCampus.Transport.Services.Design
{
    public sealed class DesignPluginSettings : IPluginSettings
    {
        public bool SortByPosition { get; set; }

        public ObservableCollection<Station> Stations { get; set; }

        public DesignPluginSettings()
        {
            SortByPosition = true;
            Stations = new ObservableCollection<Station>();
        }
    }
}
#endif