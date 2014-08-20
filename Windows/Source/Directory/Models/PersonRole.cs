// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    /// <summary>
    /// Role of a person in the directory, e.g. Professor in Section of Life Sciences.
    /// </summary>
    [ThriftStruct( "DirectoryPersonRole" )]
    public sealed class PersonRole
    {
        /// <summary>
        /// The unit.
        /// </summary>
        [ThriftField( 1, true, "extendedLocalizedUnit" )]
        public string Unit { get; set; }

        /// <summary>
        /// The title.
        /// </summary>
        [ThriftField( 2, true, "localizedTitle" )]
        public string Title { get; set; }
    }
}