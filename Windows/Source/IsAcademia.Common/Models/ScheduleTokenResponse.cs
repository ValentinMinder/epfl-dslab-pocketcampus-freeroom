// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// Response to a token request.
    /// </summary>
    [ThriftStruct( "ScheduleTokenResponse" )]
    public sealed class ScheduleTokenResponse
    {
        /// <summary>
        /// The requested token.
        /// </summary>
        [ThriftField( 1, false, "token" )]
        public ScheduleToken Token { get; set; }

        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }
    }
}