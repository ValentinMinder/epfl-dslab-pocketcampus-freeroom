// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Authentication
{
    public enum AuthenticationStatus
    {
        NotRequested,
        Authenticating,
        Success,
        WrongCredentials,
        Error
    }
}