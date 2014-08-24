// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The possible response statuses of vote requests.
    /// </summary>
    [ThriftEnum]
    public enum VoteStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success = 2,

        /// <summary>
        /// The user has already voted.
        /// </summary>
        AlreadyVoted = 1,

        /// <summary>
        /// It is too early to vote on the meal.
        /// </summary>
        TooEarly = 3,

        /// <summary>
        /// The meal is too old to be voted on.
        /// </summary>
        MealInDistantPast = 5
    }
}