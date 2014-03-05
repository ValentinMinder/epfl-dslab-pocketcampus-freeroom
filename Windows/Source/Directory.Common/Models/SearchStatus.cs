﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    /// <summary>
    /// The possible statuses of a search request.
    /// </summary>
    /// <remarks>
    /// Not part of the Thrift interface.
    /// </remarks>
    [ThriftEnum( "_" )]
    public enum SearchStatus
    {
        /// <summary>
        /// The request completed successfully.
        /// </summary>
        Success,

        /// <summary>
        /// An internal error occurred on the server.
        /// </summary>
        InternalError
    }
}