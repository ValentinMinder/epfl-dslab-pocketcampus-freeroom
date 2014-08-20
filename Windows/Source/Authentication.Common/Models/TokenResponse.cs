// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    /// <summary>
    /// Response to a request for an authentication token.
    /// </summary>
    [ThriftStruct( "AuthTokenResponse" )]
    public sealed class TokenResponse
    {
        /// <summary>
        /// The request's status.
        /// </summary>
        [ThriftField( 2, true, "statusCode" )]
        public AuthenticationStatus Status { get; set; }

        /// <summary>
        /// The authentication token.
        /// </summary>
        [ThriftField( 1, false, "tequilaToken" )]
        public string Token { get; set; }
    }
}