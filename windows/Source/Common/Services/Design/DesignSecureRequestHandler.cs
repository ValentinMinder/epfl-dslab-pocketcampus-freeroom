// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ISecureRequestHandler

#if DEBUG
using System;
using System.Threading.Tasks;
using ThinMvvm;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignSecureRequestHandler : ISecureRequestHandler
    {
        public Task<T> ExecuteAsync<T>( Func<Task<T>> attempt )
        {
            return attempt();
        }

        public void Authenticate<TViewModel>() where TViewModel : ViewModel<NoParameter> { }
    }
}
#endif