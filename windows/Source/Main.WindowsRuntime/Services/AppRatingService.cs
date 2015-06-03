// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Windows.System;

namespace PocketCampus.Main.Services
{
    public sealed class AppRatingService : IAppRatingService
    {
        private const string AppId = "28f8300e-8a84-4e3e-8d68-9a07c5b2a83a";

        public async void RequestRating()
        {
            // FRAMEWORK BUG: This seems to be the only correct way to do it.
            // Despite MSDN documentation, other ways (e.g. reviewapp without appid, or REVIEW?PFN=) open Xbox Music...
            await Launcher.LaunchUriAsync( new Uri( "ms-windows-store:reviewapp?appid=" + AppId, UriKind.Absolute ) );
        }
    }
}