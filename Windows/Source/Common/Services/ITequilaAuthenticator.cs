// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Authenticates users to Tequila.
    /// </summary>
    public interface ITequilaAuthenticator
    {
        /// <summary>
        /// Asynchronously attempts to authenticate using the specified user name and password.
        /// </summary>
        Task<bool> AuthenticateAsync( string userName, string password );

        /// <summary>
        /// Asynchronously attempts to authenticate using the specified user name and password, for the service specified by a key.
        /// </summary>
        Task<bool> AuthenticateAsync( string userName, string password, string serviceKey );

        /// <summary>
        /// Asynchronously logs off from Tequila.
        /// </summary>
        Task LogOffAsync();
    }
}