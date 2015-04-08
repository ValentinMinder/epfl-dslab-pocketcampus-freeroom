// C// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Camipro.Models
{
    [ThriftStruct( "CardStatistics" )]
    public sealed class CardStatistics
    {
        [ThriftField( 1, true, "iTotalPaymentsLastMonth" )]
        public double MonthTotal { get; set; }

        [ThriftField( 2, true, "iTotalPaymentsLastThreeMonths" )]
        public double ThreeMonthsTotal { get; set; }



        public double ThreeMonthsAverage
        {
            get { return Math.Round( ThreeMonthsTotal / 3 ); }
        }
    }
}