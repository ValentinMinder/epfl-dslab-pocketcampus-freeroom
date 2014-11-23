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
    }
}