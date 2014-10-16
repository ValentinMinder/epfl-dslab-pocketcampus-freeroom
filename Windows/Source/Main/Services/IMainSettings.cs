// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    public interface IMainSettings : IServerSettings
    {
        bool UseColoredTile { get; set; }
    }
}