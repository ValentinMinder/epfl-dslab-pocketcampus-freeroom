// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// A course offered on Moodle.
    /// </summary>
    [ThriftStruct( "MoodleCourse" )]
    public sealed class Course
    {
        /// <summary>
        /// The course's ID.
        /// </summary>
        [ThriftField( 1, true, "iId" )]
        public int Id { get; set; }

        /// <summary>
        /// The course's name.
        /// </summary>
        [ThriftField( 2, true, "iTitle" )]
        public string Name { get; set; }


        /// <summary>
        /// The course's sections.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        public CourseSection[] Sections { get; set; }
    }
}