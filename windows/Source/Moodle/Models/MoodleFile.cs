// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleFile2" )]
    public sealed class MoodleFile
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        [ThriftField( 2, true, "extension" )]
        public string Extension { get; set; }

        [ThriftField( 3, true, "url" )]
        public string DownloadUrl { get; set; }

        [ThriftField( 4, false, "icon" )]
        public string IconUrl { get; set; }


        // e.g. { "Algorithms", "General", "Books" }.
        [IgnoreDataMember]
        public string[] PathComponents { get; set; }
    }
}