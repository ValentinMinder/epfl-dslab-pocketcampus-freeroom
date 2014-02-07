// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Main.ViewModels
{
    /// <summary>
    /// Display-friendly values for the authentication statuses.
    /// </summary>
    public enum AuthenticationStatus
    {
        /// <summary>
        /// No authentication has been attempted.
        /// </summary>
        NotAuthenticated,

        /// <summary>
        /// The authentication server did not accept the provided credentials.
        /// </summary>
        WrongCredentials,

        /// <summary>
        /// There was an error contacting the authentication server.
        /// </summary>
        Error
    }
}