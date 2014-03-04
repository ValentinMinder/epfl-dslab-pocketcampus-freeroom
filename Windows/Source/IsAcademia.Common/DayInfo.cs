// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using Epfl.Mvvm;
using Epfl.Schedule.Models;

namespace Epfl.Schedule
{
    /// <summary>
    /// A binding-friendly view of a collection of courses.
    /// </summary>
    public sealed class DayInfo : ObservableObject
    {
        /// <summary>
        /// Gets the day of the week in which the courses are given.
        /// </summary>
        public DayOfWeek DayOfWeek { get; private set; }

        /// <summary>
        /// Gets the courses.
        /// </summary>
        public IList<CourseInfo> Courses { get; private set; }

        /// <summary>
        /// Creates a new DayInfo from a day, containing no courses.
        /// </summary>
        public DayInfo( DayOfWeek dayOfWeek ) : this( dayOfWeek, Enumerable.Empty<Course>() ) { }

        /// <summary>
        /// Creates a new DayInfo from a day and a sequence of courses.
        /// </summary>
        public DayInfo( DayOfWeek dayOfWeek, IEnumerable<Course> courses )
        {
            DayOfWeek = dayOfWeek;
            Courses = courses.Select( c => new CourseInfo( c ) ).ToList();
        }
    }
}