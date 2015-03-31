// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Authentication.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

// Plumbing for IAuthenticationService

namespace PocketCampus.Authentication.Services
{
    public sealed class AuthenticationService : ThriftServiceImplementation<IAuthenticationService>, IAuthenticationService
    {
        public AuthenticationService( IServerAccess access ) : base( access.CreateCommunication( "authentication" ) ) { }

        public Task<SessionResponse> GetSessionAsync( SessionRequest request )
        {
            return CallAsync<SessionRequest, SessionResponse>( x => x.GetSessionAsync, request );
        }

        public Task<LogoutResponse> DestroyAllSessionsAsync( LogoutRequest request )
        {
            return CallAsync<LogoutRequest, LogoutResponse>( x => x.DestroyAllSessionsAsync, request );
        }
    }
}