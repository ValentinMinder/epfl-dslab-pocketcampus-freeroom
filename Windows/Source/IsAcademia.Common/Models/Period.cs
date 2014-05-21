// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// Class period on a student's schedule.
    /// </summary>
    [ThriftStruct( "StudyPeriod" )]
    public sealed class Period
    {
        /// <summary>
        /// The name of the course the period is for.
        /// </summary>
        [ThriftField( 1, true, "name" )]
        public string CourseName { get; set; }

        /// <summary>
        /// The period's type.
        /// </summary>
        [ThriftField( 2, true, "periodType" )]
        public PeriodType PeriodType { get; set; }

        /// <summary>
        /// The period's start date.
        /// </summary>
        [ThriftField( 3, true, "startTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Start { get; set; }

        /// <summary>
        /// The period's end date.
        /// </summary>
        [ThriftField( 4, true, "endTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime End { get; set; }

        /// <summary>
        /// The rooms the period is taught in.
        /// </summary>
        [ThriftField( 5, true, "rooms" )]
        public string[] Rooms { get; set; }


        /// <summary>
        /// Creates a shallow clone of the period.
        /// </summary>
        /// <returns></returns>
        public Period Clone()
        {
            return (Period) this.MemberwiseClone();
        }
    }
}