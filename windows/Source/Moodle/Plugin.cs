// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.Moodle.Services;
using PocketCampus.Moodle.ViewModels;
using ThinMvvm;

namespace PocketCampus.Moodle
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "moodle"; }
        }

        public bool IsVisible
        {
            get { return true; }
        }

        public bool RequiresAuthentication
        {
            get { return true; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IMoodleService, MoodleService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel>();
        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            NavigateTo( navigationService );
        }
    }
}