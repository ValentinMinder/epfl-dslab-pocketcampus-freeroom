// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.News.Models
{
    /// <summary>
    /// The possible response statuses of a request.
    /// </summary>
    [ThriftEnum]
    public enum ResponseStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success = 200,

        /// <summary>
        /// The requested ID is invalid.
        /// </summary>
        InvalidId = 400,

        /// <summary>
        /// A network error occurred on the server while executing the request.
        /// </summary>
        NetworkError = 404
    }
}