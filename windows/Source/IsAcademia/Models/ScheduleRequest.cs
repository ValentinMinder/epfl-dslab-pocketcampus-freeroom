// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    [ThriftStruct( "ScheduleRequest" )]
    public sealed class ScheduleRequest
    {
        [ThriftField( 2, false, "weekStart" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? WeekStart { get; set; }

        [ThriftField( 3, false, "language" )]
        public string Language { get; set; }
    }
}