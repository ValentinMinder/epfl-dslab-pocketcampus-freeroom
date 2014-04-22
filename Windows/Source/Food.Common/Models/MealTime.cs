// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The times for which menus are available.
    /// </summary>
    [ThriftEnum]
    public enum MealTime
    {
        /// <summary>
        /// Lunch.
        /// </summary>
        Lunch = 1,

        /// <summary>
        /// Dinner.
        /// (there may not be any meals for dinner, depending on the day)
        /// </summary>
        Dinner = 2
    }
}