// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Satellite.Models
{
    /// <summary>
    /// The possible response statuses of server requests.
    /// </summary>
    [ThriftEnum( "SatelliteStatusCode" )]
    public enum BeerMenuStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        [ThriftEnumMember( "OK", 200 )]
        Ok = 200,

        /// <summary>
        /// A network error occurred while executing the request.
        /// </summary>
        [ThriftEnumMember( "NETWORK_ERROR", 407 )]
        NetworkError = 407
    }
}