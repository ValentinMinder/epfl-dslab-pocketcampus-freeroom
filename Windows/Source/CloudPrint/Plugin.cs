// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using ThinMvvm;

namespace PocketCampus.CloudPrint
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "CloudPrint"; }
        }

        public bool IsVisible
        {
            get { return false; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
        }

        public void NavigateTo( INavigationService navigationService )
        {

        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {

        }
    }
}