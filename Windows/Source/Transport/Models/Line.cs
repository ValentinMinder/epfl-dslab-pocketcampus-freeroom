// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    [ThriftStruct( "TransportLine" )]
    public sealed class Line
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }
    }
}