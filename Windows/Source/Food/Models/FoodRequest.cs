// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "FoodRequest" )]
    public sealed class FoodRequest
    {
        [ThriftField( 1, false, "deviceLanguage" )]
        public string Language { get; set; }

        [ThriftField( 2, false, "mealTime" )]
        public MealTime? MealTime { get; set; }

        [ThriftField( 3, false, "mealDate" )]
        [ThriftConverter( typeof( ThriftJavaDateConverter ) )]
        public DateTime? Date { get; set; }

        // Used to automatically set the price target for the user.
        [ThriftField( 5, false, "userGaspar" )]
        public string UserName { get; set; }
    }
}