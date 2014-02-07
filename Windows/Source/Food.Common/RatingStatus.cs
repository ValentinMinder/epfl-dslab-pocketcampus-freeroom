// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Food
{
    /// <summary>
    /// Display-friendly version of the rating statuses.
    /// </summary>
    public enum RatingStatus
    {
        /// <summary>
        /// Everything is OK.
        /// </summary>
        Ok,

        /// <summary>
        /// The selected meal is too old.
        /// </summary>
        MealFromThePast,

        /// <summary>
        /// The selected meal hasn't been offered yet.
        /// </summary>
        MealFromTheFuture,

        /// <summary>
        /// The user has already voted.
        /// </summary>
        AlreadyVotedToday,

        /// <summary>
        /// It is too early to vote.
        /// </summary>
        TooEarly
    }
}