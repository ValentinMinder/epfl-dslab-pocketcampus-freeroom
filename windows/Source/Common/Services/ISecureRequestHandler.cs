// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Handles requests that require authentication.
    /// </summary>
    public interface ISecureRequestHandler
    {
        /// <summary>
        /// Asynchronously executes the specified request and returns the request's result, or null if authentication failed.
        /// </summary>
        Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
            where T : class;
    }
}