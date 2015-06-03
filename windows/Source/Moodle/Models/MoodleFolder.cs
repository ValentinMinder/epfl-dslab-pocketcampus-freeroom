// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleFolder2" )]
    public sealed class MoodleFolder
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        [ThriftField( 2, true, "files" )]
        public MoodleFile[] Files { get; set; }
    }
}