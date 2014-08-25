// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// A meal available at an EPFL restaurant.
    /// </summary>
    [ThriftStruct( "EpflMeal" )]
    public sealed class Meal
    {
        /// <summary>
        /// The meal's ID.
        /// </summary>
        [ThriftField( 1, true, "mId" )]
        public long Id { get; set; }

        /// <summary>
        /// The meal's name.
        /// </summary>
        [ThriftField( 2, true, "mName" )]
        public string Name { get; set; }

        /// <summary>
        /// The meal's description.
        /// May be empty.
        /// </summary>
        [ThriftField( 3, true, "mDescription" )]
        public string Description { get; set; }

        /// <summary>
        /// The price of the meal for various price targets.
        /// </summary>
        [ThriftField( 4, true, "mPrices" )]
        public Dictionary<PriceTarget, double> Prices { get; set; }

        /// <summary>
        /// The half-portion price of the meal, if a half-portion is available.
        /// </summary>
        [ThriftField( 5, false, "mHalfPortionPrice" )]
        public double? HalfPortionPrice { get; set; }

        /// <summary>
        /// The meal's types.
        /// Meals have one or two types.
        /// </summary>
        [ThriftField( 6, true, "mTypes" )]
        public MealType[] MealTypes { get; set; }

        /// <summary>
        /// The meal's rating.
        /// </summary>
        [ThriftField( 7, true, "mRating" )]
        public Rating Rating { get; set; }


        /// <summary>
        /// The restaurant the meal is available at.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public Restaurant Restaurant { get; set; }

        /// <summary>
        /// The log ID for the meal.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        [IgnoreDataMember]
        public string LogId
        {
            get { return Id + " " + Name; }
        }

        /// <summary>
        /// Gets the price of the meal for the specified price target.
        /// </summary>
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