// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Directory.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Directory.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }
        public PersonViewModel Person { get; private set; }

        public Design()
        {
            var directoryService = new DesignDirectoryService();
            var person = directoryService.SearchAsync( null, CancellationToken.None ).Result.Results[0];

            Main = new MainViewModel( directoryService, new DesignNavigationService(), new ViewPersonRequest( "DSLAB" ) );
            Person = new PersonViewModel( new DesignBrowserService(), new DesignEmailService(), new DesignPhoneService(), new DesignContactsService(), person );

            Main.OnNavigatedTo();
            Person.OnNavigatedTo();
        }
#endif
    }
}