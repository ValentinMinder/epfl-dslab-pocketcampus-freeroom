// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleCoursesRequest2" )]
    public sealed class CoursesRequest
    {
        [ThriftField( 1, true, "language" )]
        public string Language { get; set; }
    }
}