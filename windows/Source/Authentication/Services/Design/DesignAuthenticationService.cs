// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IAuthenticationService

#if DEBUG
using System.Threading.Tasks;
using PocketCampus.Authentication.Models;

namespace PocketCampus.Authentication.Services.Design
{
    public sealed class DesignAuthenticationService : IAuthenticationService
    {
        public Task<TokenResponse> GetTokenAsync()
        {
            return Task.FromResult( new TokenResponse() );
        }

        public Task<SessionResponse> GetSessionAsync( SessionRequest request )
        {
            return Task.FromResult( new SessionResponse() );
        }

        public Task<LogoutResponse> DestroyAllSessionsAsync( LogoutRequest request )
        {
            return Task.FromResult( new LogoutResponse() );
        }
    }
}
#endif