// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMainSettings

#if DEBUG
using System;
using PocketCampus.Common.Services.Design;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignMainSettings : DesignServerSettings, IMainSettings
    {
        public Version LastUsedVersion
        {
            get { return new Version( 2, 5, 1 ); }
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