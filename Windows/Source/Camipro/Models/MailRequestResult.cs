// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Result of a request for an e-mail containing information about the e-banking payment used
    /// to charge a CAMIPRO card.
    /// </summary>
    [ThriftStruct( "SendMailResult" )]
    public sealed class MailRequestResult
    {
        /// <summary>
        /// The status of the request.
        /// </summary>
        [ThriftField( 2, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}