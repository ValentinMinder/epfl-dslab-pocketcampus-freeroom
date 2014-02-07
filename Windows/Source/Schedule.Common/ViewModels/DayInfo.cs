// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using PocketCampus.Mvvm;
using PocketCampus.Schedule.Models;

namespace PocketCampus.Schedule.ViewModels
{
    /// <summary>
    /// Display-friendly view of a collection of periods.
    /// </summary>
    public sealed class DayInfo : ObservableObject
    {
        /// <summary>
        /// Gets the day of the week in which the periods are given.
        /// </summary>
        public DateTime Date { get; private set; }

        /// <summary>
        /// Gets the periods.
        /// </summary>
        public PeriodInfo[] Periods { get; private set; }

        /// <summary>
        /// Creates a new DayInfo from a day and a sequence of periods.
        /// </summary>
        public DayInfo( StudyDay day )
        {
            Date = day.Day;
            Periods = day.Periods.Select( p => new PeriodInfo( p ) ).ToArray();
        }
    }
}