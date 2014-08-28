// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

using PocketCampus.Satellite.Services.Design;
using PocketCampus.Satellite.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Satellite.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignDataCache(), new DesignSatelliteService() ); }
        }
#endif
    }
}