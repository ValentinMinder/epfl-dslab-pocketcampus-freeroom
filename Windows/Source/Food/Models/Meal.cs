// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "EpflMeal" )]
    public sealed class Meal
    {
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
        public Restaurant Restaurant { get; set; }

        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + " " + Name; }
        }


        public double? GetPrice( PriceTarget target )
        {
            if ( Prices.ContainsKey( target ) )
            {
                return Prices[target];
            }
            if ( Prices.Any( pair => pair.Key > target ) )
            {
                return Prices.First( pair => pair.Key > target ).Value;
            }
            if ( Prices.Any() )
            {
                return Prices.First().Value;
            }
            return null;
        }
    }
}