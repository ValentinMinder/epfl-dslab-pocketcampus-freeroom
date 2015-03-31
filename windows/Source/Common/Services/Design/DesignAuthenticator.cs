// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ITequilaAuthenticator

#if DEBUG
using System.Threading.Tasks;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignAuthenticator : IAuthenticator
    {
        public Task<string> AuthenticateAsync( string userName, string password, bool rememberMe )
        {
            return Task.FromResult( "" );
        }
    }
}
#endif