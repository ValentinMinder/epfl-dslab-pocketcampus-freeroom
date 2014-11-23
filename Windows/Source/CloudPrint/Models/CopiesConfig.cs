using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "CloudPrintMultipleCopies" )]
    public sealed class CopiesConfig
    {
        [ThriftField( 1, true, "numberOfCopies" )]
        public int Count { get; set; }

        [ThriftField( 2, true, "collate" )]
        public bool Collate { get; set; }
    }
}