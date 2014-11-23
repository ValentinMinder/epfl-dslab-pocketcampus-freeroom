using System.IO;
using System.Threading.Tasks;
using PocketCampus.Common.Services;

namespace PocketCampus.CloudPrint.Services
{
    public sealed class PrintService : ThriftPrintService
    {
        private IHttpClient _client;

        public PrintService( IHttpClient client, IServerAccess access )
            : base( access )
        {
            _client = client;
        }

        // TODO create an IRawHttpClient that takes headers/params and returns streams, also use it in Moodle

        public override Task<long> UploadFileAsync( Stream file )
        {
            throw new System.NotImplementedException();
        }

        public override Task<Stream> GetPagePreviewAsync( long fileId, int pageIndex )
        {
            throw new System.NotImplementedException();
        }
    }
}