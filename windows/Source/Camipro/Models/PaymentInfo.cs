// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "CardLoadingithEbankingInfo" )]
    public sealed class PaymentInfo
    {
        [ThriftField( 1, true, "iPaidTo" )]
        public string AccountName { get; set; }

        [ThriftField( 2, true, "iAccountNumber" )]
        public string AccountNumber { get; set; }

        [ThriftField( 3, true, "iReferenceNumber" )]
        public string ReferenceNumber { get; set; }
    }
}