// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// Response to a course list request.
    /// </summary>
    [ThriftStruct( "CoursesListReply" )]
    public sealed class CourseListResponse
    {
        /// <summary>
        /// The courses.
        /// </summary>
        [ThriftField( 1, false, "iCourses" )]
        public Course[] Courses { get; set; }

        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}