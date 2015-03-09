// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IPluginLoader

#if DEBUG
using System.Collections.Generic;
using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.Main.Services.Design
{
    public sealed class DesignPluginLoader : IPluginLoader
    {
        public IPlugin[] GetPlugins()
        {
            return new IPlugin[]
            {
                new DesignPlugin( "Camipro" ),
                new DesignPlugin( "Directory" ),
                new DesignPlugin( "Events"  ),
                new DesignPlugin( "Food" ),
                new DesignPlugin( "IsAcademia" ),
                new DesignPlugin( "Map" ),
                new DesignPlugin( "Moodle" ),
                new DesignPlugin( "News" ),
                new DesignPlugin( "Satellite" ),
                new DesignPlugin( "Transport" )
            };
        }

        private sealed class DesignPlugin : IPlugin
        {
            public string Id { get; set; }
            public bool IsVisible { get; set; }
            public string Name { get; set; } // for the designer
            public bool RequiresAuthentication { get; set; }

            public DesignPlugin( string name )
            {
                Id = Name = name;
                IsVisible = true;
            }

            public void Initialize( INavigationService navigationService ) { }

            public void NavigateTo( INavigationService navigationService ) { }

            public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
        }
    }
}
#endif