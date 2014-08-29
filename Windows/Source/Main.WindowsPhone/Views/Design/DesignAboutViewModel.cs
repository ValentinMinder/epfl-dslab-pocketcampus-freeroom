// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for AboutViewModel

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.Main.Services.Design;
#endif
using PocketCampus.Main.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Main.Views.Design
{
    public sealed class DesignAboutViewModel : DesignViewModel<AboutViewModel, NoParameter>
    {
#if DEBUG
        protected override AboutViewModel ViewModel
        {
            get { return new AboutViewModel( new DesignBrowserService(), new DesignEmailService(), new DesignRatingService() ); }
        }
#endif
    }
}