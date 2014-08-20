// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleCourseSection2" )]
    public sealed class CourseSection
    {
        private const string DatesFormat = "{0:M} - {1:M}";
        private const string DatesSeparator = " - ";


        [ThriftField( 1, true, "resources" )]
        public MoodleResource[] Resources { get; set; }

        [ThriftField( 2, false, "title" )]
        public string Title { get; set; }

        [ThriftField( 3, false, "startDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? StartDate { get; set; }

        [ThriftField( 4, false, "endDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? EndDate { get; set; }

        [ThriftField( 5, false, "details" )]
        public string Details { get; set; }


        /// <summary>
        /// Display title of the section.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public string DisplayTitle
        {
            get { return Title ?? string.Format( DatesFormat, StartDate.Value, EndDate.Value ); }
        }
    }
}