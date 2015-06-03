// Copyright (c) PocketCampus.Org 2014-15
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
        /// Asynchronously gets a session for the specified token (once it has been authenticated).
        /// </summary>
        [ThriftMethod( "getOAuth2TokensFromCode" )]
        Task<SessionResponse> GetSessionAsync( [ThriftParameter( 1, "req" )] SessionRequest request );
    }
}