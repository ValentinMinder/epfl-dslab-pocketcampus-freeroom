// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Windows.ApplicationModel;
using Windows.System;

namespace PocketCampus.Main.Services
{
    public sealed class AppRatingService : IAppRatingService
    {
        public async void RequestRating()
        {
            await Launcher.LaunchUriAsync( new Uri( "ms-windows-store:REVIEW?PFN=" + Package.Current.Id.FamilyName ) );
        }
    }
}