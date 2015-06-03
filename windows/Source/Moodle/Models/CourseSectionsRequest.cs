// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleCourseSectionsRequest2" )]
    public sealed class CourseSectionsRequest
    {
        [ThriftField( 1, true, "language" )]
        public string Language { get; set; }

        [ThriftField( 2, true, "courseId" )]
        public int CourseId { get; set; }
    }
}