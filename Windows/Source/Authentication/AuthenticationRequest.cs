// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Authentication
{
    /// <summary>
    /// Request for the authentication ViewModel.
    /// </summary>
    public sealed class AuthenticationRequest
    {
        /// <summary>
        /// Gets the action that should be executed after the authentication succeeds, if any.
        /// If set, this will override the normal "go back" action.
        /// </summary>
        public Action SuccessAction { get; private set; }


        /// <summary>
        /// Creates a new AuthenticationRequest.
        /// </summary>
        public AuthenticationRequest( Action successAction = null )
        {
            SuccessAction = successAction;
        }
    }
}