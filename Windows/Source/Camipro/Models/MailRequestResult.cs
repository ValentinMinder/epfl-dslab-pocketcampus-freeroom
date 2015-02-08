// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "SendMailResult" )]
    public sealed class MailRequestResult
    {
        [ThriftField( 2, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}