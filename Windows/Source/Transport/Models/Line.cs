// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Transport.Models
{
    /// <summary>
    /// Transport line.
    /// </summary>
    [ThriftStruct( "TransportLine" )]
    public sealed class Line
    {
        /// <summary>
        /// The line's name.
        /// </summary>
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }
    }
}