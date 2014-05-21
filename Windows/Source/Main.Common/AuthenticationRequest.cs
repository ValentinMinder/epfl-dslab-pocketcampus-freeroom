// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Main
{
    /// <summary>
    /// Request for the authentication ViewModel.
    /// </summary>
    public sealed class AuthenticationRequest
    {
        /// <summary>
        /// Gets a value indicating whether credentials should be allowed to be saved.
        /// </summary>
        public bool CanSaveCredentials { get; private set; }

        /// <summary>
        /// Gets the action that should be executed after the authentication succeeds, if any.
        /// If set, this will override the normal "go back" action.
        /// </summary>
        public Action SuccessAction { get; private set; }


        /// <summary>
        /// Creates a new AuthenticationRequest.
        /// </summary>
        public AuthenticationRequest( bool canSaveCredentials, Action successAction = null )
        {
            CanSaveCredentials = canSaveCredentials;
            SuccessAction = successAction;
        }
    }
}