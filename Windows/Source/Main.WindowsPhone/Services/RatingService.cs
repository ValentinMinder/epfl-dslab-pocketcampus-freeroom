// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Phone.Tasks;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Requests that the user rate the app.
    /// </summary>
    public sealed class RatingService : IRatingService
    {
        /// <summary>
        /// Request an app rating from the user.
        /// </summary>
        public void RequestRating()
        {
            new MarketplaceReviewTask().Show();
        }
    }
}