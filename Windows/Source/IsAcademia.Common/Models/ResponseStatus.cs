// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    /// <summary>
    /// The possible response statuses of server requests.
    /// </summary>
    [ThriftEnum]
    public enum ResponseStatus
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
        AuthenticationError = 407,

        /// <summary>
        /// An error was thrown by IS-Academia while executing the request.
        /// </summary>
        IsAcademiaError = 418
    }
}