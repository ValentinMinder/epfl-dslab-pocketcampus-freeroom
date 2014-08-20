// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Statistics about a CAMIPRO card spending.
    /// </summary>
    [ThriftStruct( "CardStatistics" )]
    public sealed class CardStatistics
    {
        /// <summary>
        /// The total spending over the last month.
        /// </summary>
        [ThriftField( 1, true, "iTotalPaymentsLastMonth" )]
        public double MonthTotal { get; set; }

        /// <summary>
        /// The total spending over the last three months.
        /// </summary>
        [ThriftField( 2, true, "iTotalPaymentsLastThreeMonths" )]
        public double ThreeMonthsTotal { get; set; }


        /// <summary>
        /// The average spending over the last three months.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public double ThreeMonthsAverage
        {
            get { return Math.Round( ThreeMonthsTotal / 3 ); }
        }
    }

    /// <summary>
    /// Information about an e-banking payment to charge a CAMIPRO card.
    /// </summary>
    [ThriftStruct( "CardLoadingithEbankingInfo" )]
    public sealed class PaymentInfo
    {
        /// <summary>
        /// The account name.
        /// </summary>
        [ThriftField( 1, true, "iPaidTo" )]
        public string AccountName { get; set; }

        /// <summary>
        /// The account number.
        /// </summary>
        [ThriftField( 2, true, "iAccountNumber" )]
        public string AccountNumber { get; set; }

        /// <summary>
        /// The reference number.
        /// </summary>
        [ThriftField( 3, true, "iReferenceNumber" )]
        public string ReferenceNumber { get; set; }
    }

    /// <summary>
    /// Contains information about the e-banking payment used to charge a CAMIPRO card,
    /// as well as information to help the user decide how much to pay.
    /// </summary>
    [ThriftStruct( "StatsAndLoadingInfo" )]
    public sealed class EbankingInfo
    {
        /// <summary>
        /// Spending statistics of the card.
        /// </summary>
        [ThriftField( 1, false, "iCardStatistics" )]
        public CardStatistics CardStatistics { get; set; }

        /// <summary>
        /// Information about the e-banking payment used to charge the card.
        /// </summary>
        [ThriftField( 2, false, "iCardLoadingWithEbankingInfo" )]
        public PaymentInfo PaymentInfo { get; set; }

        /// <summary>
        /// The status of the request that fetched the information.
        /// </summary>
        [ThriftField( 3, true, "iStatus" )]
        public ResponseStatus Status { get; set; }
    }
}