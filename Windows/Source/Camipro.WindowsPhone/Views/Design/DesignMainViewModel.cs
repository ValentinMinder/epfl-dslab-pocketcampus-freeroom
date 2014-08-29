// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

#if DEBUG
using PocketCampus.Camipro.Services.Design;
using PocketCampus.Common.Services.Design;
#endif
using PocketCampus.Camipro.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Camipro.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignCamiproService(), new DesignSecureRequestHandler() ); }
        }
#endif
    }
}