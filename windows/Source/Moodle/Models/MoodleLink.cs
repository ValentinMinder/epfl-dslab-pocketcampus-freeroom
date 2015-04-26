// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodleUrl2" )]
    public sealed class MoodleLink
    {
        [ThriftField( 1, true, "name" )]
        public string Name { get; set; }

        [ThriftField( 2, true, "url" )]
        public string Url { get; set; }
    }
}