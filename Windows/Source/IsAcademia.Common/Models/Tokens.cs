// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Plumbing for authentication

using PocketCampus.Common;

namespace PocketCampus.IsAcademia.Models
{
    public sealed class AuthenticationToken : IAuthenticationToken
    {
        public string AuthenticationKey { get; private set; }

        public AuthenticationToken( string key )
        {
            AuthenticationKey = key;
        }
    }
}