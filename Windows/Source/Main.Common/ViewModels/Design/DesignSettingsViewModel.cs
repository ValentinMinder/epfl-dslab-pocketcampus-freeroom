// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for the SettingsViewModel

using System.Collections.Generic;
using PocketCampus.Common;
namespace PocketCampus.Main.ViewModels.Design
{
    public sealed class DesignSettingsViewModel
    {
#if DEBUG
        public IMainSettings Settings { get { return new DesignMainSettings(); } }

        private sealed class DesignMainSettings : IMainSettings
        {
            public bool IsFirstRun
            {
                get { return false; }
                set { }
            }

            public ServerConfiguration ServerConfiguration
            {
                get { return new ServerConfiguration( "https", 4433, "Moodle", "Food" ); }
                set { }
            }

            public bool IsAuthenticated
            {
                get { return true; }
                set { }
            }

            public string UserName
            {
                get { return "johndoe"; }
                set { }
            }

            public string Password
            {
                get { return "12345"; }
                set { }
            }

            public Dictionary<string, string> Sessions
            {
                get { return null; }
                set { }
            }
        }
#endif
    }
}