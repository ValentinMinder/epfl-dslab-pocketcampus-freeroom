// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// The possible response statuses of server requests.
    /// </summary>
    [ThriftEnum]
    public enum BeerMenuStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success = 200,

        /// <summary>
        /// A network error occurred while executing the request.
        /// </summary>
        NetworkError = 407
    }
}