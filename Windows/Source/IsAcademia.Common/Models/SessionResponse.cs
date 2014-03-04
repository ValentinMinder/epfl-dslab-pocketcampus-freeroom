// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// Response to a session request.
    /// </summary>
    [ThriftStruct( "IsaSessionResponse" )]
    public sealed class SessionResponse
    {
        /// <summary>
        /// The requested token.
        /// </summary>
        [ThriftField( 1, false, "sessionId" )]
        public string Session { get; set; }

        /// <summary>
        /// The request status.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }
    }
}