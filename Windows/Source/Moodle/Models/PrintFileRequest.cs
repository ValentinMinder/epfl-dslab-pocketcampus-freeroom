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