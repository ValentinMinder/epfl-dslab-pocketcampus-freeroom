// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

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