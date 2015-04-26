// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using PocketCampus.CloudPrint.Services;
using PocketCampus.CloudPrint.ViewModels;
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
            get { return true; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IPrintService, PrintService>();

            Messenger.Register<PrintRequest>( navigationService.NavigateTo<MainViewModel, PrintRequest> );
        }

        // This plugin cannot be navigated to directly
        public void NavigateTo( INavigationService navigationService )
        {
            throw new NotSupportedException();
        }

        // This plugin cannot be navigated to directly
        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            throw new NotSupportedException();
        }
    }
}