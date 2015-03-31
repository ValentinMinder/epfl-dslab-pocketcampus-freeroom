// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using ThinMvvm;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Handles requests that require authentication.
    /// </summary>
    public interface ISecureRequestHandler
    {
        /// <summary>
        /// Asynchronously executes the specified request.
        /// </summary>
        Task<T> ExecuteAsync<T>( Func<Task<T>> attempt );

        /// <summary>
        /// Requests new credentials from the user.
        /// If authentication is successful, comes back to a new instance of the ViewModel.
        /// </summary>
        void Authenticate<TViewModel>()
            where TViewModel : ViewModel<NoParameter>;
    }
}