// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;
using Windows.System;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Provides access to the Windows Phone browser.
    /// </summary>
    public sealed class BrowserService : IBrowserService
    {
        /// <summary>
        /// Navigates to the specified URL.
        /// </summary>
        public async void NavigateTo( string url )
        {
            // If it's a PocketCampus URL, take action instead of quitting the app and opening a new instance of it
            if ( !App.UriMapper.NavigateToCustomUri( url ) )
            {
                await Launcher.LaunchUriAsync( new Uri( url, UriKind.Absolute ) );
            }
        }
    }
}