// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

#if DEBUG
using System.ComponentModel;

namespace PocketCampus.Map.Services.Design
{
    public sealed class DesignPluginSettings : IPluginSettings
    {
        public bool UseGeolocation
        {
            get { return false; }
            set { }
        }

#pragma warning disable 0067 // event never used
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif