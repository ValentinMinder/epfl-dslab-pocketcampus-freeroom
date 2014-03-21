// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Mvvm;

// Design data for the MainViewModel

namespace PocketCampus.Main.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public IPlugin[] Plugins
        {
            get
            {
                return new[]
                {
                    new DesignPlugin( "Cat", "http://lorempixel.com/500/500/cats/" ),
                    new DesignPlugin( "Food", "http://lorempixel.com/500/500/food/" ),
                    new DesignPlugin( "Kitten", "http://lorempixel.com/501/501/cats/" ),
                    new DesignPlugin( "Om nom nom", "http://lorempixel.com/501/501/food/" ),
                    new DesignPlugin( "Meow", "http://lorempixel.com/502/502/cats/" ),
                    new DesignPlugin( "Tasty!", "http://lorempixel.com/502/502/food/" ),
                    new DesignPlugin( "Kitty", "http://lorempixel.com/503/503/cats/" )
                };
            }
        }


        // This also "implements" IWindowsPhonePlugin for the WP designer
        private sealed class DesignPlugin : IPlugin
        {
            public string Id { get; set; }
            public string Name { get; set; }
            public bool RequiresAuthentication { get; set; }
            public Uri Icon { get; set; }
            public Uri SmallIcon { get; set; }

            public DesignPlugin( string name, string iconUrl )
            {
                Id = Name = name;
                Icon = SmallIcon = new Uri( iconUrl, UriKind.Absolute );
            }

            public void Initialize( INavigationService navigationService ) { }

            public void NavigateTo( INavigationService navigationService ) { }

            public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
        }
#endif
    }
}