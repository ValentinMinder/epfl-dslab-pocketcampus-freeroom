// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Moodle.Services.Design;
#endif
using PocketCampus.Moodle.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Moodle.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), new DesignNavigationService(), new DesignMoodleService() ); }
        }
#endif
    }
}