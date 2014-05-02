// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Moodle.Models
{
    /// <summary>
    /// The possible response statuses of server requests.
    /// </summary>
    /// <remarks>
    /// Not in the Thrift interface (it uses undocumented constants).
    /// </remarks>
    [ThriftEnum]
    public enum ResponseStatus
    {
        /// <summary>
        /// The request was successfully completed.
        /// </summary>
        Success = 200,

        /// <summary>
        /// A network error occurred while executing the request.
        /// </summary>
        NetworkError = 404,

        /// <summary>
        /// A course ID was required but not received.
        /// </summary>
        UnsetCourseId = 405,

        /// <summary>
        /// The provided credentials are invalid or expired.
        /// </summary>
        AuthenticationError = 407
    }
}