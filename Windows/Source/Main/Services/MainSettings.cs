// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Contains application-level settings.
    /// </summary>
    public sealed class MainSettings : SettingsBase<MainSettings>, IMainSettings
    {
        /// <summary>
        /// Gets or sets the user's session status.
        /// </summary>
        public SessionStatus SessionStatus
        {
            get { return Get<SessionStatus>(); }
            set { Set( value ); }
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
        /// Gets or sets a value indicating whether the app should use a colored tile, as opposed to a white one.
        /// </summary>
        public bool UseColoredTile
        {
            get { return Get<bool>(); }
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
        public MainSettings( ISettingsStorage settings ) : base( settings ) { }


        /// <summary>
        /// Gets the default values.
        /// </summary>
        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.Configuration, () => new ServerConfiguration( "https", "prod-pocketcampus.epfl.ch", 14611, 
                                                                       "Camipro", "Directory", "Events", "Food", "IsAcademia", 
                                                                       "Map", "Moodle", "News", "Satellite", "Transport" ) },
                { x => x.SessionStatus, () => SessionStatus.NotLoggedIn },
                { x => x.UseColoredTile, () => true },
                { x => x.Session, () => null },
                { x => x.Sessions, () => new Dictionary<string, string>() }
            };
        }
    }
}