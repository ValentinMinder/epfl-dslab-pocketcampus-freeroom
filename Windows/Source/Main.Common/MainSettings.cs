// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;

namespace PocketCampus.Main
{
    /// <summary>
    /// Contains various application-level settings.
    /// </summary>
    public sealed class MainSettings : SettingsBase<MainSettings>, IMainSettings
    {
        /// <summary>
        /// Gets or sets a value indicating whether the user is authenticated.
        /// </summary>
        public bool IsAuthenticated
        {
            get { return Get<bool>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the GASPAR username (or the SCIPER number).
        /// </summary>
        public string UserName
        {
            get { return GetEncrypted(); }
            set { SetEncrypted( value ); }
        }

        /// <summary>
        /// Gets or sets the GASPAR password.
        /// </summary>
        public string Password
        {
            get { return GetEncrypted(); }
            set { SetEncrypted( value ); }
        }

        /// <summary>
        /// Gets or sets the saved sessions.
        /// </summary>
        public Dictionary<string, string> Sessions
        {
            get { return Get<Dictionary<string, string>>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Gets or sets the session for the server. (separate from the per-plugin sessions)
        /// </summary>
        public string Session
        {
            get { return Get<string>(); }
            set { Set( value ); }
        }

        /// <summary>
        /// Gets or sets the server configuration used to connect.
        /// </summary>
        public ServerConfiguration Configuration
        {
            get { return Get<ServerConfiguration>(); }
            set { Set( value ); }
        }


        /// <summary>
        /// Creates a new instance of PluginSettings.
        /// </summary>
        public MainSettings( IApplicationSettings settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values.
        /// </summary>
        protected override SettingsDefaultValues<MainSettings> GetDefaultValues()
        {
            return new SettingsDefaultValues<MainSettings>
            {
                { x => x.Configuration, () => new ServerConfiguration( "https", 4433, "Camipro", "Directory", "Events", "Food", "IsAcademia", 
                                                                                      "Map", "Moodle", "News", "Satellite", "Transport" ) },
                { x => x.IsAuthenticated, () => false },
                { x => x.UserName, () => null },
                { x => x.Password, () => null },
                { x => x.Session, () => null },
                { x => x.Sessions, () => new Dictionary<string, string>() }
            };
        }
    }
}