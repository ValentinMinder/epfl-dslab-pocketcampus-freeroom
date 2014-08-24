// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    /// <summary>
    /// The available search periods for events.
    /// </summary>
    public enum SearchPeriod
    {
        /// <summary>
        /// One day.
        /// </summary>
        OneDay = 1,

        /// <summary>
        /// Two days.
        /// </summary>
        TwoDays = 2,

        /// <summary>
        /// One week.
        /// </summary>
        OneWeek = 7,

        /// <summary>
        /// One month. (approximately)
        /// </summary>
        OneMonth = 30,

        /// <summary>
        /// Six months. (approximately)
        /// </summary>
        SixMonths = 180,

        /// <summary>
        /// One year. (non-leap)
        /// </summary>
        OneYear = 365
    }
}