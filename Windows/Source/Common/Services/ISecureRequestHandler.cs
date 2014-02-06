// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Mvvm;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Handles requests that require two-step authentication.
    /// </summary>
    public interface ISecureRequestHandler
    {
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