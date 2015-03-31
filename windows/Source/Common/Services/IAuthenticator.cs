// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Authenticates users.
    /// </summary>
    public interface IAuthenticator
    {
        /// <summary>
        /// Authenticates with the specified credentials, and returns a session or null if the authentication failed.
        /// </summary>
        Task<string> AuthenticateAsync( string userName, string password, bool rememberMe );

        /// <summary>
        /// Logs off all devices for the specified session.
        /// </summary>
        Task LogOutAsync( string session );
    }
}