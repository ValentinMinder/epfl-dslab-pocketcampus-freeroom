// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// Section of a Moodle course.
    /// </summary>
    [ThriftStruct( "MoodleSection" )]
    public sealed class CourseSection
    {
        /// <summary>
        /// The files in the section.
        /// </summary>
        [ThriftField( 1, true, "iResources" )]
        public CourseFile[] Files { get; set; }

        /// <summary>
        /// The section's name.
        /// </summary>
        [ThriftField( 2, true, "iText" )]
        public string Name { get; set; }
    }
}