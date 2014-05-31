// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Main.Models
{
    [ThriftStruct( "AuthSessionRequest" )]
    public sealed class AuthenticationSessionRequest
    {
        [ThriftField( 1, true, "tequilaToken" )]
        public string TequilaToken { get; set; }

        [ThriftField( 2, false, "rememberMe" )]
        public bool? RememberMe { get; set; }
    }
}