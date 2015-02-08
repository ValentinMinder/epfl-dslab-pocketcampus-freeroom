// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "StatsAndLoadingInfo" )]
    public sealed class EbankingInfo
    {
        [ThriftField( 1, false, "iCardStatistics" )]
        public CardStatistics CardStatistics { get; set; }

        [ThriftField( 2, false, "iCardLoadingWithEbankingInfo" )]
        public PaymentInfo PaymentInfo { get; set; }

        [ThriftField( 3, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}