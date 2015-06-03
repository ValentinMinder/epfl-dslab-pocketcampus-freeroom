// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Provides access to a Web browser.
    /// </summary>
    public interface IBrowserService
    {
        /// <summary>
        /// Navigates to the specified URL.
        /// </summary>
        void NavigateTo( string url );
    }
}