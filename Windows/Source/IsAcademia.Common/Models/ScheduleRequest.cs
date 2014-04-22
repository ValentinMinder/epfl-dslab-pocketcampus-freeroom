// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// Request information to get a one-week schedule.
    /// </summary>
    [ThriftStruct( "ScheduleRequest" )]
    public sealed class ScheduleRequest
    {
        /// <summary>
        /// The start of the week.
        /// </summary>
        [ThriftField( 2, false, "weekStart" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? WeekStart { get; set; }

        /// <summary>
        /// The request language.
        /// </summary>
        [ThriftField( 3, false, "language" )]
        public string Language { get; set; }
    }
}