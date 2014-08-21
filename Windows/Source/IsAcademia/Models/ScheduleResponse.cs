// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// Response to a schedule request.
    /// </summary>
    [ThriftStruct( "ScheduleResponse" )]
    public sealed class ScheduleResponse
    {
        /// <summary>
        /// The days of the schedule.
        /// </summary>
        /// <remarks>
        /// At least 5 (Monday to Friday), at most 7.
        /// </remarks>
        [ThriftField( 1, false, "days" )]
        public StudyDay[] Days { get; set; }

        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }
    }
}