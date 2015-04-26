// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    [ThriftStruct( "FoodResponse" )]
    public sealed class FoodResponse
    {
        [ThriftField( 1, false, "menu" )]
        public Restaurant[] Menu { get; set; }

        [ThriftField( 2, false, "userStatus" )]
        public PriceTarget? UserPriceTarget { get; set; }

        [ThriftField( 4, true, "statusCode" )]
        public FoodStatus Status { get; set; }
    }
}