// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

#if DEBUG
using PocketCampus.Directory.Services.Design;
#endif
using PocketCampus.Directory.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Directory.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, ViewPersonRequest>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignDirectoryService(), new DesignNavigationService(), new ViewPersonRequest( "DSLAB" ) ); }
        }
#endif
    }
}