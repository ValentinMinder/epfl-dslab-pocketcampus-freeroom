// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Main.Models
{
    /// <summary>
    /// Response to a request for an authentication session.
    /// </summary>
    [ThriftStruct( "AuthSessionResponse" )]
    public sealed class AuthenticationSessionResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public AuthenticationStatusCode Status { get; set; }

        /// <summary>
        /// The authentication session.
        /// </summary>
        [ThriftField( 1, false, "sessionId" )]
        public string Session { get; set; }
    }
}