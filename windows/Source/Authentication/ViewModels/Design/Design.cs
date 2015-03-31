// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Common.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Authentication.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }

        public Design()
        {
            Main = new MainViewModel( new DesignAuthenticator(), new DesignNavigationService(),
                                      new DesignServerSettings(), new DesignCredentialsStorage(),
                                      new AuthenticationRequest( () => { } ) );

            Main.OnNavigatedTo();
        }
#endif
    }
}