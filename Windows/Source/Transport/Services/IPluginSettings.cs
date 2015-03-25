// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.ObjectModel;
using PocketCampus.Transport.Models;

namespace PocketCampus.Transport.Services
{
    public interface IPluginSettings
    {
        bool SortByPosition { get; set; }

        ObservableCollection<Station> Stations { get; set; }
    }
}