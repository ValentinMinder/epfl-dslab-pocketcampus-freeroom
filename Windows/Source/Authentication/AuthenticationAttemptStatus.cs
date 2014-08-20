// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Authentication
{
    /// <summary>
    /// Display-friendly values for the authentication attempt statuses.
    /// </summary>
    public enum AuthenticationAttemptStatus
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