// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Windows.System;

namespace PocketCampus.Main.Services
{
    public sealed class RatingService : IRatingService
    {
        // from Package.appxmanifest; do not change!
        private const string AppPackageFamilyName = "a1ad3481-9e1b-455d-9d6b-cb6fd6cb0d94_292wqxwpch5dy";

        public async void RequestRating()
        {
            await Launcher.LaunchUriAsync( new Uri( "ms-windows-store:REVIEW?PFN=" + AppPackageFamilyName ) );
        }
    }
}