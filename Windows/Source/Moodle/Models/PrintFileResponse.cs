// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    [ThriftStruct( "MoodlePrintFileResponse2" )]
    public sealed class PrintFileResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public MoodleStatus Status { get; set; }

        [ThriftField( 2, false, "printJobId" )]
        public long? DocumentId { get; set; }
    }
}