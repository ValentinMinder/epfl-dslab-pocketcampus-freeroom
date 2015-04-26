// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;

namespace PocketCampus.Authentication
{
    public sealed class AuthenticationRequest
    {
        /// <summary>
        /// If set, this will override the normal "go back" action after successfully authenticating.
        /// </summary>
        public Action SuccessAction { get; private set; }


        public AuthenticationRequest( Action successAction = null )
        {
            SuccessAction = successAction;
        }
    }
}