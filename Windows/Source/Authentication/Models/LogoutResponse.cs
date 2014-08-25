﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    [ThriftStruct( "LogoutResponse" )]
    public sealed class LogoutResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public AuthenticationStatus Status { get; set; }
    }
}