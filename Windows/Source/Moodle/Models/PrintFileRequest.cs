// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodlePrintFileRequest2" )]
    public sealed class PrintFileRequest
    {
        [ThriftField( 1, true, "fileUrl" )]
        public string FileUrl { get; set; }
    }
}