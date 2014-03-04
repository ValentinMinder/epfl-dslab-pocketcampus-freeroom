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
        [ThriftMethod( "getAuthSessionId" )]
        Task<AuthenticationSessionResponse> GetSessionAsync( [ThriftParameter( 1, "tequilaToken" )] string token );
    }
}