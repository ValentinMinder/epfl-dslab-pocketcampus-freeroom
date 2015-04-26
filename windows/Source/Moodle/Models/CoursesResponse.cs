// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleCoursesResponse" )]
    public sealed class CoursesResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public MoodleStatus Status { get; set; }

        [ThriftField( 2, true, "courses" )]
        public Course[] Courses { get; set; }
    }
}