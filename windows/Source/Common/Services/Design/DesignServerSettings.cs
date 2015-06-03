// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IServerSettings

using System.Collections.Generic;
#if DEBUG
using System.ComponentModel;

namespace PocketCampus.Common.Services.Design
{
    public class DesignServerSettings : IServerSettings
    {
        public ServerConfiguration Configuration
        {
            get
            {
                return new ServerConfiguration
                {
                    EnabledPlugins = new[] { "Authentication", "Camipro", "Directory", "Events", "Food", "IsAcademia", "Map", "Moodle", "News", "Satellite", "Transport" }
                };
            }
            set { }
        }

        public string Session { get; set; }

        public Dictionary<string, string> Sessions { get; set; }

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