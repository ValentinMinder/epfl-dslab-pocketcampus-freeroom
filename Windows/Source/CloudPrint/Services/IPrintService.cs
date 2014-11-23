using System.IO;
using System.Threading.Tasks;
using PocketCampus.CloudPrint.Models;
using ThriftSharp;

namespace PocketCampus.CloudPrint.Services
{
    [ThriftService( "CloudPrintService" )]
    public interface IPrintService
    {
        [ThriftMethod( "printDocument" )]
        Task<PrintDocumentResponse> PrintAsync( [ThriftParameter( 1, "request" )] PrintDocumentRequest request );

        [ThriftMethod( "printPreview" )]
        Task<PrintPreviewDocumentResponse> PreviewPrintAsync( [ThriftParameter( 1, "request" )] PrintDocumentRequest request );

        Task<long> UploadFileAsync( Stream file );

        Task<Stream> GetPagePreviewAsync( long fileId, int pageIndex );
    }
}