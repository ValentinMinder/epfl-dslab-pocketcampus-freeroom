// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    // e.g. Professor in Section of Life Sciences.
    [ThriftStruct( "DirectoryPersonRole" )]
    public sealed class PersonRole
    {
        [ThriftField( 1, true, "extendedLocalizedUnit" )]
        public string Unit { get; set; }

        [ThriftField( 2, true, "localizedTitle" )]
        public string Title { get; set; }
    }
}