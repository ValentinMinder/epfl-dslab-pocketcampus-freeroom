// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMainSettings

#if DEBUG
using PocketCampus.Common.Services.Design;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignMainSettings : DesignServerSettings, IMainSettings
    {
        public TileColoring TileColoring
        {
            get { return TileColoring.ColorOnTransparent; }
            set { }
        }
    }
}
#endif