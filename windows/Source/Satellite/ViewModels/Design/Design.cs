// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Satellite.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Satellite.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }

        public Design()
        {
            Main = new MainViewModel( new DesignDataCache(), new DesignSatelliteService() );

            Main.OnNavigatedToAsync();
        }
#endif
    }
}