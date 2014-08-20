// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Authentication.Models
{
    /// <summary>
    /// Status for authentication requests.
    /// </summary>
    [ThriftEnum]
    public enum AuthenticationStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success = 200,

        /// <summary>
        /// A network error occurred while executing the request.
        /// </summary>
        NetworkError = 404,

        /// <summary>
        /// The provided credentials are invalid or expired.
        /// </summary>
        AuthenticationError = 407
    }
}