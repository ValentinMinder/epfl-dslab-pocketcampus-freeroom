// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    [ThriftStruct( "LogoutRequest" )]
    public sealed class LogoutRequest
    {
        [ThriftField( 1, true, "sessionId" )]
        public string Session { get; set; }
    }
}