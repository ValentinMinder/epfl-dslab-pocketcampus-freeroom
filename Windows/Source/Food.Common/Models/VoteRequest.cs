// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Request to vote on a meal.
    /// </summary>
    [ThriftStruct( "VoteRequest" )]
    public sealed class VoteRequest
    {
        /// <summary>
        /// The meal's ID.
        /// </summary>
        [ThriftField( 1, true, "mealId" )]
        public long MealId { get; set; }

        /// <summary>
        /// The rating.
        /// </summary>
        [ThriftField( 2, true, "rating" )]
        public double RatingValue { get; set; }

        /// <summary>
        /// The ID of the user's device.
        /// </summary>
        [ThriftField( 3, true, "deviceId" )]
        public string DeviceId { get; set; }
    }
}