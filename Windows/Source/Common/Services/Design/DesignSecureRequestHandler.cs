﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for SecureRequestHandler

using System;
using System.Threading.Tasks;
using ThinMvvm;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignSecureRequestHandler : ISecureRequestHandler
    {
        public Task<T> ExecuteAsync<T>( Func<Task<T>> attempt ) where T : class
        {
            return attempt();
        }

        public Task ExecuteAsync<TViewModel, TToken, TSession>( ITwoStepAuthenticator<TToken, TSession> authenticator, Func<TSession, Task> attempt )
            where TViewModel : ViewModel<NoParameter>
            where TToken : IAuthenticationToken
            where TSession : class
        {
            return Task.FromResult( 0 );
        }

        public void Authenticate<TViewModel>() where TViewModel : ViewModel<NoParameter> { }
    }
}