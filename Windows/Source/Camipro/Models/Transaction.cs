// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// A transaction performed with a CAMIPRO card.
    /// </summary>
    [ThriftStruct( "Transaction" )]
    public sealed class Transaction
    {
        /// <summary>
        /// The date and time at which the transaction was performed.
        /// </summary>
        [ThriftField( 1, true, "iDate" )]
        [ThriftConverter( typeof( CamiproDateConverter ) )]
        public DateTime Date { get; set; }

        /// <summary>
        /// The place at which the transaction was performed.
        /// </summary>
        [ThriftField( 3, true, "iPlace" )]
        public string Place { get; set; }

        /// <summary>
        /// The amount of money spent or received.
        /// </summary>
        [ThriftField( 4, true, "iAmount" )]
        public double Amount { get; set; }
    }
}