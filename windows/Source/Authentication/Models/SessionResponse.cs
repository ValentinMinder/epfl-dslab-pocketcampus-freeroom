// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    [ThriftStruct( "AuthSessionResponse" )]
    public sealed class SessionResponse
    {
        [ThriftField( 2, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        [ThriftField( 1, false, "sessionId" )]
        public string Session { get; set; }
    }
}