// Copyright (c) PocketCampus.Org 2014
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
        /// The request asynchronously returns a boolean indicating whether the authentication succeeded.
        /// </summary>
        /// <remarks>
        /// For use with the new, HTTP header based authentication.
        /// </remarks>
        Task ExecuteAsync<TViewModel>( Func<Task<bool>> attempt )
            where TViewModel : IViewModel<NoParameter>;

        /// <summary>
        /// Asynchronously executes the specified request, with the specified authenticator, for the specified ViewModel type.
        /// The request asynchronously returns a boolean indicating whether the authentication succeeded.
        /// </summary>
        Task ExecuteAsync<TViewModel, TToken, TSession>( ITwoStepAuthenticator<TToken, TSession> authenticator, Func<TSession, Task<bool>> attempt )
            where TViewModel : IViewModel<NoParameter>
            where TToken : IAuthenticationToken
            where TSession : class;
    }
}