// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
using PocketCampus.Mvvm;
using PocketCampus.Schedule.Models;

namespace PocketCampus.Schedule.ViewModels
{
    /// <summary>
    /// Display-friendly display of a period.
    /// </summary>
    public sealed class PeriodInfo : ObservableObject
    {
        /// <summary>
        /// Gets the name of the course.
        /// </summary>
        public string CourseName { get; private set; }

        /// <summary>
        /// Gets the type of the period.
        /// </summary>
        public PeriodType PeriodType { get; private set; }

        /// <summary>
        /// Gets a string representing all the rooms in which the period can be undertaken.
        /// </summary>
        public string Rooms { get; private set; }

        /// <summary>
        /// Gets the start hour of the period.
        /// This may not be the exact start time.
        /// </summary>
        public int StartHour { get; private set; }

        /// <summary>
        /// Gets the length, in hours, of the period.
        /// </summary>
        public int Length { get; private set; }

        /// <summary>
        /// Gets the hour starts contained within the period, not including the last one.
        /// </summary>
        public int[] ContainedHours { get; private set; }

        /// <summary>
        /// Creates a PeriodInfo from a Period.
        /// </summary>
        public PeriodInfo( Period period )
        {
            CourseName = period.CourseName;
            PeriodType = period.PeriodType;
            Rooms = string.Join( ", ", period.Rooms );
            StartHour = period.Start.Hour;
            Length = period.End.Hour - period.Start.Hour;
            ContainedHours = Enumerable.Range( StartHour, Length ).ToArray();
        }
    }
}