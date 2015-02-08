// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

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
    }
}