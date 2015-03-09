// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.CloudPrint.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.CloudPrint.Services
{
    public sealed class PrintService : ThriftServiceImplementation<IPrintService>, IPrintService
    {
        public PrintService( IServerAccess access ) : base( access.CreateCommunication( "cloudprint" ) ) { }

        public Task<PrintDocumentResponse> PrintAsync( PrintDocumentRequest request )
        {
            return CallAsync<PrintDocumentRequest, PrintDocumentResponse>( x => x.PrintAsync, request );
        }
    }
}