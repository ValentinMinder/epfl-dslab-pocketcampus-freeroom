// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Main.Models;
using ThriftSharp;

// Plumbing for IAuthenticationService

namespace PocketCampus.Main.Services
{
    public sealed class AuthenticationService : ThriftServiceImplementation<IAuthenticationService>, IAuthenticationService
    {
        public AuthenticationService( IServerAccess access ) : base( access.CreateCommunication( "authentication" ) ) { }

        public Task<AuthenticationTokenResponse> GetTokenAsync()
        {
            return CallAsync<AuthenticationTokenResponse>( x => x.GetTokenAsync );
        }

        public Task<AuthenticationSessionResponse> GetSessionAsync( AuthenticationSessionRequest request )
        {
            return CallAsync<AuthenticationSessionRequest, AuthenticationSessionResponse>( x => x.GetSessionAsync, request );
        }

        public Task<AuthenticationLogoutResponse> DestroyAllSessionsAsync( AuthenticationLogoutRequest request )
        {
            return CallAsync<AuthenticationLogoutRequest, AuthenticationLogoutResponse>( x => x.DestroyAllSessionsAsync, request );
        }
    }
}