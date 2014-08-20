// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Main.Models;
using ThriftSharp;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Authenticates the user to the PocketCampus server.
    /// </summary>
    [ThriftService( "AuthenticationService" )]
    public interface IAuthenticationService
    {
        /// <summary>
        /// Asynchronously gets a token.
        /// </summary>
        [ThriftMethod( "getAuthTequilaToken" )]
        Task<AuthenticationTokenResponse> GetTokenAsync();

        /// <summary>
        /// Asynchronously gets a session for the specified token (once it has been authenticated).
        /// </summary>
        [ThriftMethod( "getAuthSession" )]
        Task<AuthenticationSessionResponse> GetSessionAsync( [ThriftParameter( 1, "req" )] AuthenticationSessionRequest request );


        /// <summary>
        /// Asynchronously requests that all user sessions for PocketCampus be destroyed.
        /// </summary>
        [ThriftMethod( "destroyAllUserSessions" )]
        Task<AuthenticationLogoutResponse> DestroyAllSessionsAsync( [ThriftParameter( 1, "req" )] AuthenticationLogoutRequest request );
    }
}