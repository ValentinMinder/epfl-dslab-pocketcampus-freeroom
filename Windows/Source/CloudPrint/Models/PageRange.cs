using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "CloudPrintPageRange" )]
    public sealed class PageRange
    {
        [ThriftField( 1, true, "pageFrom" )]
        public int From { get; set; }

        [ThriftField( 2, true, "pageTo" )]
        public int To { get; set; }
    }
}