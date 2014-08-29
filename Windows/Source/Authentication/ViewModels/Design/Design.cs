// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Authentication.Services.Design;
using PocketCampus.Common.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Authentication.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public AuthenticationViewModel Authentication { get; private set; }

        public Design()
        {
            Authentication =
                new AuthenticationViewModel( new DesignAuthenticationService(), new DesignAuthenticator(), new DesignServerAccess(),
                                             new DesignNavigationService(), new DesignServerSettings(), new DesignCredentialsStorage(),
                                             new AuthenticationRequest() );

            Authentication.OnNavigatedTo();
        }
#endif
    }
}