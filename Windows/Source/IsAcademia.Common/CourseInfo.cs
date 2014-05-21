// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using Epfl.Mvvm;
using Epfl.Schedule.Models;

namespace Epfl.Schedule
{
    /// <summary>
    /// A binding-friendly display of the <see cref="Course"/> class.
    /// </summary>
    public sealed class CourseInfo : ObservableObject
    {
        /// <summary>
        /// Gets the name of the course.
        /// </summary>
        public string Name { get; private set; }

        /// <summary>
        /// Gets the type of the course.
        /// </summary>
        public CourseType Type { get; private set; }

        /// <summary>
        /// Gets a string representing all the rooms in which the course is given.
        /// </summary>
        public string Rooms { get; private set; }

        /// <summary>
        /// Gets the start hour of the course.
        /// This may not be the exact start time.
        /// </summary>
        public int StartHour { get; private set; }

        /// <summary>
        /// Gets the length, in hours, of the course.
        /// </summary>
        public int Length { get; private set; }

        /// <summary>
        /// Creates a CourseInfo from a Course.
        /// </summary>
        public CourseInfo( Course course )
        {
            Name = course.Name;
            Type = course.Type;
            Rooms = string.Join( ", ", course.Rooms );
            StartHour = course.Start.Hour;
            Length = course.End.Hour - course.Start.Hour;
        }
    }
}