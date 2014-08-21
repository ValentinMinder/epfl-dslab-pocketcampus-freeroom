// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for the AuthenticationViewModel

namespace PocketCampus.Authentication.ViewModels.Design
{
    public sealed class DesignAuthenticationViewModel
    {
#if DEBUG
        public string UserName { get { return "johndoe"; } }
        public string Password { get { return "12345"; } }

        public bool SaveCredentials { get { return true; } }
        public bool CanSaveCredentials { get { return true; } }

        public bool IsAuthenticating { get { return false; } }

        public AuthenticationAttemptStatus Status { get { return AuthenticationAttemptStatus.WrongCredentials; } }
#endif
    }
}