// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Phone.Tasks;
using PocketCampus.Common.Services;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Provides access to the Windows Phone e-mail client.
    /// </summary>
    public sealed class EmailService : IEmailService
    {
        /// <summary>
        /// Asks the user to send an e-mail to the specified e-mail address.
        /// </summary>
        public void ComposeEmail( string emailAddress )
        {
            new EmailComposeTask { To = emailAddress }.Show();
        }
    }
}