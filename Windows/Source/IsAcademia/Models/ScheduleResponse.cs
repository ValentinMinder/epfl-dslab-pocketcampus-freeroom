// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    [ThriftStruct( "ScheduleResponse" )]
    public sealed class ScheduleResponse
    {
        [ThriftField( 1, false, "days" )]
        public StudyDay[] Days { get; set; }

        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }
    }
}