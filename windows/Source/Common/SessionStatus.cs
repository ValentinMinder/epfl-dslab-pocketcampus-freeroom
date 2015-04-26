// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common
{
    /// <summary>
    /// The possible session statuses of the user.
    /// </summary>
    public enum SessionStatus
    {
        /// <summary>
        /// The user is not logged in.
        /// </summary>
        NotLoggedIn,

        /// <summary>
        /// The user is logged in, but wishes not to stay so after the end of the application session.
        /// </summary>
        /// <remarks>
        /// "Session" doesn't have a very clear meaning, since users rarely close apps explicitly; rather, they 
        /// put apps in the background and forget about them temporarily.
        /// </remarks>
        LoggedInTemporarily,

        /// <summary>
        /// The user is logged in.
        /// </summary>
        LoggedIn
    }
}