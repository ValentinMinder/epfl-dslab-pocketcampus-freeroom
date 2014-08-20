// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// A file available for download on Moodle.
    /// </summary>
    [ThriftStruct( "MoodleResource" )]
    public sealed class CourseFile
    {
        /// <summary>
        /// The file's name.
        /// </summary>
        [ThriftField( 1, true, "iName" )]
        public string Name { get; set; }

        /// <summary>
        /// The file's download URL.
        /// </summary>
        [ThriftField( 2, true, "iUrl" )]
        public string Url { get; set; }


        /// <summary>
        /// The course the file belongs to.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public Course Course { get; set; }
    }
}