// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Request for menus.
    /// </summary>
    [ThriftStruct( "FoodRequest" )]
    public sealed class FoodRequest
    {
        /// <summary>
        /// The language of the request.
        /// French by default.
        /// </summary>
        [ThriftField( 1, false, "deviceLanguage" )]
        public string Language { get; set; }

        /// <summary>
        /// The meal time of the request.
        /// Lunch by default.
        /// </summary>
        [ThriftField( 2, false, "mealTime" )]
        public MealTime? MealTime { get; set; }

        /// <summary>
        /// The date of the request.
        /// Today by default.
        /// </summary>
        [ThriftField( 3, false, "mealDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? Date { get; set; }

        /// <summary>
        /// The user's username, if the user is authenticated.
        /// </summary>
        /// <remarks>
        /// This is used to automatically set the price target for the user.
        /// </remarks>
        [ThriftField( 5, false, "userGaspar" )]
        public string UserName { get; set; }
    }
}