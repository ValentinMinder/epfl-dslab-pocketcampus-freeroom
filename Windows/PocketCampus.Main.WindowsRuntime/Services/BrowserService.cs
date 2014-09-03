// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;
using Windows.System;

namespace PocketCampus.Main.Services
{
    public sealed class BrowserService : IBrowserService
    {
        public async void NavigateTo( string url )
        {
            await Launcher.LaunchUriAsync( new Uri( url ) );
        }
    }
}