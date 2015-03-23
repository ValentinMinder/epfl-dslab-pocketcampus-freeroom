// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMainSettings

#if DEBUG
using PocketCampus.Common.Services.Design;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignMainSettings : DesignServerSettings, IMainSettings
    {
        public string LastUsedVersion
        {
            get { return "2.5.0"; }
            set { }
        }

        public TileColoring TileColoring
        {
            get { return TileColoring.ColorOnTransparent; }
            set { }
        }
    }
}
#endif