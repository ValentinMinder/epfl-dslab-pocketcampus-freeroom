// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Reflection;
using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.Main.Services
{
    public sealed class MainSettings : SettingsBase<MainSettings>, IMainSettings
    {
        public SessionStatus SessionStatus
        {
            get { return Get<SessionStatus>(); }
            set { Set( value ); }
        }

        public Dictionary<string, string> Sessions
        {
            get { return Get<Dictionary<string, string>>(); }
            set { Set( value ); }
        }

        public string Session
        {
            get { return Get<string>(); }
            set { Set( value ); }
        }

        public ServerConfiguration Configuration
        {
            get { return Get<ServerConfiguration>(); }
            set { Set( value ); }
        }

        public Version LastUsedVersion
        {
            get { return Get<Version>(); }
            set { Set( value ); }
        }

        public TileColoring TileColoring
        {
            get { return Get<TileColoring>(); }
            set { Set( value ); }
        }


        public MainSettings( ISettingsStorage settings ) : base( settings ) { }


        protected override SettingsDefaultValues GetDefaultValues()
        {
            return new SettingsDefaultValues
            {
                { x => x.Configuration, () => new ServerConfiguration( "https", "prod-pocketcampus.epfl.ch", 14611, 
                                                                       "Camipro", "Directory", "Events", "Food", "IsAcademia", 
                                                                       "Map", "Moodle", "News", "Satellite", "Transport" ) },
                { x => x.SessionStatus, () => SessionStatus.NotLoggedIn },
                { x => x.Session, () => null },
                { x => x.Sessions, () => new Dictionary<string, string>() },
                { x => x.TileColoring, () => TileColoring.FullColors },
                { x => x.LastUsedVersion, () => typeof( MainSettings ).GetTypeInfo().Assembly.GetName().Version }
            };
        }
    }
}