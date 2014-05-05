// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using ThinMvvm;

// Design data for the MainViewModel

namespace PocketCampus.Main.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public DataStatus DataStatus { get { return DataStatus.DataLoaded; } }

        public IPlugin[] Plugins
        {
            get
            {
                return new[]
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
        }


        // This also "implements" IWindowsPhonePlugin for the WP designer
        private sealed class DesignPlugin : IPlugin
        {
            public string Id { get; set; }
            public string Name { get; set; }
            public bool RequiresAuthentication { get; set; }
            public string IconKey { get; set; }

            public DesignPlugin( string name )
            {
                Id = Name = name;
                IconKey = name + "Icon";
            }

            public void Initialize( INavigationService navigationService ) { }

            public void NavigateTo( INavigationService navigationService ) { }

            public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
        }
#endif
    }
}