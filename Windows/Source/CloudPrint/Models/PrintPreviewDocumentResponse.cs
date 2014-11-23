using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "PrintPreviewDocumentResponse" )]
    public sealed class PrintPreviewDocumentResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public ResponseStatus Status { get; set; }

        [ThriftField( 2, false, "numberOfPages" )]
        public int? PagesCount { get; set; }
    }
}