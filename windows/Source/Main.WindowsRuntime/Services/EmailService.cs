// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common.Services;
using Windows.System;

namespace PocketCampus.Main.Services
{
    public sealed class EmailService : IEmailService
    {
        public async void ComposeEmail( string emailAddress )
        {
            await Launcher.LaunchUriAsync( new Uri( "mailto:" + emailAddress ) );
        }
    }
}