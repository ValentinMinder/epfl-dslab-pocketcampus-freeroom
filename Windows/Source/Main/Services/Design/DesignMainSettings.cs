// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMainSettings

#if DEBUG
using System.Collections.Generic;
using PocketCampus.Common.Services.Design;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignMainSettings : DesignServerSettings, IMainSettings
    {
        public Dictionary<string, string> Sessions { get; set; }

        public bool UseColoredTile { get; set; }
    }
}
#endif