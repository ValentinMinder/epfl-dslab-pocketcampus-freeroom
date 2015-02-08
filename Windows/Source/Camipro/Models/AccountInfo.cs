// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "BalanceAndTransactions" )]
    public sealed class AccountInfo
    {
        // In CHF
        [ThriftField( 1, false, "iBalance" )]
        public double? Balance { get; set; }

        [ThriftField( 2, false, "iTransactions" )]
        public Transaction[] Transactions { get; set; }

        [ThriftField( 4, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}