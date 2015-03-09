// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Camipro.Models.Thrift;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "Transaction" )]
    public sealed class Transaction
    {
        [ThriftField( 1, true, "iDate" )]
        [ThriftConverter( typeof( CamiproDateConverter ) )]
        public DateTime Date { get; set; }

        [ThriftField( 3, true, "iPlace" )]
        public string Place { get; set; }

        [ThriftField( 4, true, "iAmount" )]
        public double Amount { get; set; }
    }
}