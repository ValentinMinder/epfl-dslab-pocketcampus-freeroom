// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    // Union type: exactly 1 property is set.
    [ThriftStruct( "MoodleResource2" )]
    public sealed class MoodleResource
    {
        [ThriftField( 1, false, "file" )]
        public MoodleFile File { get; set; }

        [ThriftField( 2, false, "folder" )]
        public MoodleFolder Folder { get; set; }

        [ThriftField( 3, false, "url" )]
        public MoodleLink Link { get; set; }
    }
}