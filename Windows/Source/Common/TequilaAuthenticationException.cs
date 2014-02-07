// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Common
{
    /// <summary>
    /// Raised when an error occurs during Tequila authentication because of invalid credentials.
    /// </summary>
    public sealed class TequilaAuthenticationException : Exception
    {
        public TequilaAuthenticationException()
            : base( "The provided Tequila credentials are invalid." )
        {
        }
    }
}