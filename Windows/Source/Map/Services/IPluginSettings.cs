// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;

namespace PocketCampus.Map.Services
{
    public interface IPluginSettings : INotifyPropertyChanged
    {
        bool UseGeolocation { get; set; }
    }
}