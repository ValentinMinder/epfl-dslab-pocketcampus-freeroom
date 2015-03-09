// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Provides two-step authentication.
    /// </summary>
    /// <typeparam name="TToken">The type of the token containing a Tequila key.</typeparam>
    /// <typeparam name="TSession">The type of the session that can be gotten from an authenticated token, saved locally and be reused..</typeparam>
    public interface ITwoStepAuthenticator<TToken, TSession>
        where TToken : IAuthenticationToken
    {
        /// <summary>
        /// Asynchronously gets a new token.
        /// </summary>
        Task<TToken> GetTokenAsync();

        /// <summary>
        /// Gets a session from an authenticated token (i.e. a token after authentication was performed).
        /// </summary>
        Task<TSession> GetSessionAsync( TToken token );
    }
}