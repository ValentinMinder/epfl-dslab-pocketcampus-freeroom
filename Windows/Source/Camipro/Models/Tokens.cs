// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "CamiproSession" )]
    public sealed class CamiproSession
    {
        [ThriftField( 1, true, "camiproCookie" )]
        public string Cookie { get; set; }
    }
}