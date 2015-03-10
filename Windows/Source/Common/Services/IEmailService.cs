// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Provides access to an e-mail client.
    /// </summary>
    public interface IEmailService
    {
        /// <summary>
        /// Asks the user to send an e-mail to the specified e-mail address.
        /// </summary>
        void ComposeEmail( string emailAddress );
    }
}