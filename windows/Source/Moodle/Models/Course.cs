// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleCourse2" )]
    public sealed class Course
    {
        [ThriftField( 1, true, "courseId" )]
        public int Id { get; set; }

        [ThriftField( 2, true, "name" )]
        public string Name { get; set; }

        /// <summary>
        /// Gets the log ID.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + "-" + Name; }
        }
    }
}