using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "PrintDocumentResponse" )]
    public sealed class PrintDocumentResponse
    {
        [ThriftField( 1, true, "statusCode" )]
        public ResponseStatus Status { get; set; }
    }
}