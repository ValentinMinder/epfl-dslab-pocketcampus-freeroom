// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// The response to a courses section request.
    /// </summary>
    [ThriftStruct( "SectionsListReply" )]
    public sealed class CourseSectionListResponse
    {
        /// <summary>
        /// The sections.
        /// </summary>
        [ThriftField( 1, false, "iSections" )]
        public CourseSection[] Sections { get; set; }

        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}