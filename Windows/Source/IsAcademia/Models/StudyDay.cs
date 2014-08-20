// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// A schedule day.
    /// </summary>
    [ThriftStruct( "StudyDay" )]
    public sealed class StudyDay
    {
        /// <summary>
        /// The day's date.
        /// </summary>
        [ThriftField( 1, true, "day" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Day { get; set; }


        /// <summary>
        /// The day's periods.
        /// </summary>
        /// <remarks>
        /// May be empty.
        /// </remarks>
        [ThriftField( 2, true, "periods" )]
        public Period[] Periods { get; set; }
    }
}