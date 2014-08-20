// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Main.Models
{
    [ThriftStruct( "LogoutResponse" )]
    public sealed class AuthenticationLogoutResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public AuthenticationStatus Status { get; set; }
    }
}