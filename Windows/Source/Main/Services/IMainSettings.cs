// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    public interface IMainSettings : IServerSettings
    {
        Dictionary<string, string> Sessions { get; set; }

        bool UseColoredTile { get; set; }
    }
}