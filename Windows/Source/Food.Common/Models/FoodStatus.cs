// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The possible response statuses of the food request.
    /// </summary>
    [ThriftEnum( "FoodStatusCode" )]
    public enum FoodStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        [ThriftEnumMember( "OK", 200 )]
        Success,

        /// <summary>
        /// A network error occurred on the server while executing the request.
        /// </summary>
        [ThriftEnumMember( "NETWORK_ERROR", 404 )]
        NetworkError
    }
}