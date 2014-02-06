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
        /// The request status.
        /// </summary>
        [ThriftField( 4, true, "statusCode" )]
        public FoodStatus Status { get; set; }
    }
}