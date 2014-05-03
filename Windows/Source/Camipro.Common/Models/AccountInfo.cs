// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Information about a CAMIPRO account.
    /// </summary>
    [ThriftStruct( "BalanceAndTransactions" )]
    [DataContract]
    public sealed class AccountInfo
    {
        /// <summary>
        /// The current balance, in Swiss Francs.
        /// </summary>
        [ThriftField( 1, false, "iBalance" )]
        [DataMember]
        public double? Balance { get; set; }

        /// <summary>
        /// The last transactions.
        /// </summary>
        /// <remarks>
        /// At most 10.
        /// </remarks>
        [ThriftField( 2, false, "iTransactions" )]
        [DataMember]
        public Transaction[] Transactions { get; set; }

        /// <summary>
        /// The status of the request that fetched the account.
        /// </summary>
        [ThriftField( 4, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}