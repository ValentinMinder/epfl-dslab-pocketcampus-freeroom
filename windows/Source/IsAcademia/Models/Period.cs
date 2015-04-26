// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    [ThriftStruct( "StudyPeriod" )]
    public sealed class Period
    {
        [ThriftField( 1, true, "name" )]
        public string CourseName { get; set; }

        [ThriftField( 2, true, "periodType" )]
        public PeriodType PeriodType { get; set; }

        [ThriftField( 3, true, "startTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime Start { get; set; }

        [ThriftField( 4, true, "endTime" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime End { get; set; }

        [ThriftField( 5, true, "rooms" )]
        public string[] Rooms { get; set; }


        public Period Clone()
        {
            return (Period) MemberwiseClone();
        }
    }
}