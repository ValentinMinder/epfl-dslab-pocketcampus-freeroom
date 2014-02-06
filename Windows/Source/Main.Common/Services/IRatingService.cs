// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Requests that the user rate the app.
    /// </summary>
    public interface IRatingService
    {
        /// <summary>
        /// Request an app rating from the user.
        /// </summary>
        void RequestRating();
    }
}