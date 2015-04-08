// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Storage for credentials, which should be encrypted.
    /// </summary>
    public interface ICredentialsStorage
    {
        /// <summary>
        /// Gets the user name.
        /// </summary>
        string UserName { get; }

        /// <summary>
        /// Gets the password.
        /// </summary>
        string Password { get; }

        /// <summary>
        /// Sets the user name and the password.
        /// </summary>
        void SetCredentials( string userName, string password );

        /// <summary>
        /// Deletes the stored credentials.
        /// </summary>
        void DeleteCredentials();
    }
}