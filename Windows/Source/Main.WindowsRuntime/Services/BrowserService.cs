// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    public sealed class BrowserService : IBrowserService
    {
        public void NavigateTo( string url )
        {
            LauncherEx.Launch( new Uri( url, UriKind.Absolute ) );
        }
    }
}