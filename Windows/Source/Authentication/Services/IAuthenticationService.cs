﻿// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Authentication.Models;
using ThriftSharp;

namespace PocketCampus.Authentication.Services
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
        Task<TokenResponse> GetTokenAsync();

        /// <summary>
        /// Asynchronously gets a session for the specified token (once it has been authenticated).
        /// </summary>
        [ThriftMethod( "getAuthSession" )]
        Task<SessionResponse> GetSessionAsync( [ThriftParameter( 1, "req" )] SessionRequest request );


        /// <summary>
        /// Asynchronously requests that all user sessions for PocketCampus be destroyed.
        /// </summary>
        [ThriftMethod( "destroyAllUserSessions" )]
        Task<LogoutResponse> DestroyAllSessionsAsync( [ThriftParameter( 1, "req" )] LogoutRequest request );
    }
}