// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Food.Models;

namespace PocketCampus.Food
{
    /// <summary>
    /// Information about a rating request, passed to the RatingViewModel.
    /// </summary>
    public sealed class RatingInfo
    {
        /// <summary>
        /// Gets the meal to be rated.
        /// </summary>
        public Meal Meal { get; private set; }

        /// <summary>
        /// Gets the time at which the meal is available.
        /// </summary>
        public MealTime MealTime { get; private set; }

        /// <summary>
        /// Gets the date at which the meal is available.
        /// </summary>
        public DateTime MealDate { get; private set; }


        /// <summary>
        /// Creates a new RatingInfo.
        /// </summary>
        public RatingInfo( Meal meal, MealTime mealTime, DateTime mealDate )
        {
            Meal = meal;
            MealTime = mealTime;
            MealDate = mealDate;
        }
    }
}