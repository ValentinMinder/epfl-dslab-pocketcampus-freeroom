// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Main
{
    /// <summary>
    /// The possible authentication statuses of the user.
    /// </summary>
    public enum AuthenticationStatus
    {
        /// <summary>
        /// The user is not authenticated.
        /// </summary>
        NotAuthenticated,

        /// <summary>
        /// The user is authenticated, but wishes not to stay so after the end of the application session.
        /// </summary>
        /// <remarks>
        /// "Session" doesn't have a very clear meaning, since users rarely close apps explicitly; rather, they 
        /// put apps in the background and forget about them temporarily.
        /// </remarks>
        AuthenticatedTemporarily,

        /// <summary>
        /// The user is authenticated.
        /// </summary>
        Authenticated
    }
}