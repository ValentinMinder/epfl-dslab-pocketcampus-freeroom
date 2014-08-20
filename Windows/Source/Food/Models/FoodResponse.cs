// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// Response for menu requests.
    /// </summary>
    [ThriftStruct( "FoodResponse" )]
    public sealed class FoodResponse
    {
        /// <summary>
        /// The menu.
        /// </summary>
        [ThriftField( 1, false, "menu" )]
        public Restaurant[] Menu { get; set; }

        /// <summary>
        /// The user's price target, present only if the username was sent with the request.
        /// </summary>
        [ThriftField( 2, false, "userStatus" )]
        public PriceTarget? UserPriceTarget { get; set; }

        /// <summary>
        /// The request status.
        /// </summary>
        [ThriftField( 4, true, "statusCode" )]
        public FoodStatus Status { get; set; }
    }
}