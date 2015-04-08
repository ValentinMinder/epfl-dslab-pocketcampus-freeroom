// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    [ThriftStruct( "AuthTokenResponse" )]
    public sealed class TokenResponse
    {
        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        [ThriftField( 1, false, "tequilaToken" )]
        public string Token { get; set; }
    }
}