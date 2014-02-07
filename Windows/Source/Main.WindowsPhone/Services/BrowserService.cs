// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Microsoft.Phone.Tasks;
using PocketCampus.Common.Services;

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
        public void NavigateTo( string url )
        {
            new WebBrowserTask { Uri = new Uri( url, UriKind.Absolute ) }.Show();
        }
    }
}