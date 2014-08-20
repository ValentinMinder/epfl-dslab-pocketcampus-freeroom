// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Main.Models
{
    [ThriftStruct( "LogoutRequest" )]
    public sealed class AuthenticationLogoutRequest
    {
        [ThriftField( 1, true, "sessionId" )]
        public string Session { get; set; }
    }
}