// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Events
{
    // In hours
    public enum SearchPeriod
    {
        Now = 4,
        OneDay = 1 * 24,
        TwoDays = 2 * 24,
        OneWeek = 7 * 24,
        OneMonth = 30 * 24,
        SixMonths = 180 * 24,
        OneYear = 365 * 24
    }
}