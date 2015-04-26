// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using ThinMvvm;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "EpflMeal" )]
    public sealed class Meal : ObservableObject
    {
        private double? _currentPrice;


        [ThriftField( 1, true, "mId" )]
        public long Id { get; set; }

        [ThriftField( 2, true, "mName" )]
        public string Name { get; set; }

        [ThriftField( 3, true, "mDescription" )]
        public string Description { get; set; }

        [ThriftField( 4, true, "mPrices" )]
        public Dictionary<PriceTarget, double> Prices { get; set; }

        [ThriftField( 5, false, "mHalfPortionPrice" )]
        public double? HalfPortionPrice { get; set; }

        [ThriftField( 6, true, "mTypes" )]
        public MealType[] MealTypes { get; set; }

        [ThriftField( 7, true, "mRating" )]
        public Rating Rating { get; set; }


        [IgnoreDataMember]
        public double? CurrentPrice
        {
            get { return _currentPrice; }
            private set { SetProperty( ref _currentPrice, value ); }
        }

        [IgnoreDataMember]
        public Restaurant Restaurant { get; set; }

        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + " " + Name; }
        }


        public void SetCurrentPrice( PriceTarget target )
        {
            if ( Prices.ContainsKey( target ) )
            {
                CurrentPrice = Prices[target];
            }
            else if ( Prices.Any( pair => pair.Key > target ) )
            {
                CurrentPrice = Prices.First( pair => pair.Key > target ).Value;
            }
            else if ( Prices.Any() )
            {
                CurrentPrice = Prices.First().Value;
            }
            else
            {
                CurrentPrice = null;
            }
        }
    }
}