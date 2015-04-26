// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Common
{
    /// <summary>
    /// Minimal interface for the authentication tokens.
    /// </summary>
    public interface IAuthenticationToken
    {
        /// <summary>
        /// Gets the authentication key associated with the token.
        /// </summary>
        string AuthenticationKey { get; }
    }
}