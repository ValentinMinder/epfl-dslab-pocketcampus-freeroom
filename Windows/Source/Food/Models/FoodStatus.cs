// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Food.Models
{
    /// <summary>
    /// The possible response statuses of the food request.
    /// </summary>
    [ThriftEnum]
    public enum FoodStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success = 200,

        /// <summary>
        /// A network error occurred on the server while executing the request.
        /// </summary>
        NetworkError = 404
    }
}