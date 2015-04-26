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
        /// Asynchronously executes the specified request for the specified ViewModel type.
        /// </summary>
        /// <remarks>
        /// For use with the new, HTTP header based authentication.
        /// </remarks>
        Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
            where T : class;

        /// <summary>
        /// Asynchronously executes the specified request, with the specified authenticator, for the specified ViewModel type.
        /// </summary>
        Task ExecuteAsync<TViewModel, TToken, TSession>( ITwoStepAuthenticator<TToken, TSession> authenticator, Func<TSession, Task> attempt )
            where TViewModel : ViewModel<NoParameter>
            where TToken : IAuthenticationToken
            where TSession : class;

        /// <summary>
        /// Requests new credentials from the user.
        /// If authentication is successful, comes back to a new instance of the ViewModel.
        /// </summary>
        void Authenticate<TViewModel>()
            where TViewModel : ViewModel<NoParameter>;
    }
}