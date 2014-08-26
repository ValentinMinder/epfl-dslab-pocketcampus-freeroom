﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    /// <summary>
    /// The available search periods for events.
    /// </summary>
    /// <remarks>
    /// * 24 since the period is in hours.
    /// </remarks>
    public enum SearchPeriod
    {
        /// <summary>
        /// One day.
        /// </summary>
        OneDay = 1 * 24,

        /// <summary>
        /// Two days.
        /// </summary>
        TwoDays = 2 * 24,

        /// <summary>
        /// One week.
        /// </summary>
        OneWeek = 7 * 24,

        /// <summary>
        /// One month. (approximately)
        /// </summary>
        OneMonth = 30 * 24,

        /// <summary>
        /// Six months. (approximately)
        /// </summary>
        SixMonths = 180 * 24,

        /// <summary>
        /// One year. (non-leap)
        /// </summary>
        OneYear = 365 * 24
    }
}