// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// The possible response statuses of a request.
    /// </summary>
    [ThriftEnum( "NewsStatusCode" )]
    public enum ResponseStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        [ThriftEnumMember( "OK", 200 )]
        Success,

        /// <summary>
        /// The requested ID is invalid.
        /// </summary>
        [ThriftEnumMember( "INVALID_ID", 400 )]
        InvalidId,

        /// <summary>
        /// A network error occurred on the server while executing the request.
        /// </summary>
        [ThriftEnumMember( "NETWORK_ERROR", 404 )]
        NetworkError
    }
}