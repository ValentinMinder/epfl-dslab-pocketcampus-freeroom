using System.Threading.Tasks;
using PocketCampus.CloudPrint.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.CloudPrint.Services
{
    public abstract class ThriftPrintService : ThriftServiceImplementation<IPrintService>, IPrintService
    {
        protected ThriftPrintService( IServerAccess access ) : base( access.CreateCommunication( "cloudprint" ) ) { }

        public Task<PrintDocumentResponse> PrintAsync( PrintDocumentRequest request )
        {
            return CallAsync<PrintDocumentRequest, PrintDocumentResponse>( x => x.PrintAsync, request );
        }

        public Task<PrintPreviewDocumentResponse> PreviewPrintAsync( PrintDocumentRequest request )
        {
            return CallAsync<PrintDocumentRequest, PrintPreviewDocumentResponse>( x => x.PreviewPrintAsync, request );
        }
    }
}