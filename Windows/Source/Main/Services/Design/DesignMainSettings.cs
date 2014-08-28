// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IMainSettings

#if DEBUG
using System.Collections.Generic;
using System.ComponentModel;
using PocketCampus.Common;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignMainSettings : IMainSettings
    {
        public Dictionary<string, string> Sessions { get; set; }

        public bool UseColoredTile { get; set; }

        public ServerConfiguration Configuration { get; set; }

        public string Session { get; set; }

        public SessionStatus SessionStatus
        {
            get { return SessionStatus.LoggedInTemporarily; }
            set { }
        }

#pragma warning disable 0067 // unused event
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067
    }
}
#endif