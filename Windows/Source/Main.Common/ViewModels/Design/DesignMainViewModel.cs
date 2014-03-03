// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Windows.Input;
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
                    new DesignPlugin( "A plugin", false ),
                    new DesignPlugin( "Another", false ),
                    new DesignPlugin( "Kittens!", true ),
                    new DesignPlugin( "Meow?", false ),
                };
            }
        }

        public ICommand OpenPluginCommand
        {
            get { return new Command<IPlugin>( this, _ => { }, p => p == null || !( (IPlugin) p ).RequiresAuthentication ); }
        }


        // This also "implements" IWindowsPhonePlugin for the WP designer
        private sealed class DesignPlugin : IPlugin
        {
            public string Id { get; private set; }
            public string Name { get; private set; }
            public bool RequiresAuthentication { get; private set; }
            public Uri Icon { get; private set; }
            public Uri SmallIcon { get; private set; }

            public DesignPlugin( string name, bool requiresAuth )
            {
                Id = Name = name;
                RequiresAuthentication = requiresAuth;
                Icon = SmallIcon = new Uri( "http://lorempixel.com/500/500/cats/", UriKind.Absolute );
            }

            public void Initialize( INavigationService navigationService ) { }

            public void NavigateTo( INavigationService navigationService ) { }

            public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService ) { }
        }
#endif
    }
}