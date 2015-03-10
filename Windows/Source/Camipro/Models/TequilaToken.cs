// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "TequilaToken" )]
    public sealed class TequilaToken : IAuthenticationToken
    {
        [ThriftField( 1, true, "iTequilaKey" )]
        public string AuthenticationKey { get; set; }

        [ThriftField( 2, false, "loginCookie" )]
        public string LoginCookie { get; set; }
    }
}