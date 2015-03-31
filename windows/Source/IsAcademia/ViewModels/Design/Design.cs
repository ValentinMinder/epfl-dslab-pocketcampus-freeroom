// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using PocketCampus.Common.Services.Design;
using PocketCampus.IsAcademia.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.IsAcademia.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }

        public Design()
        {
            Main = new MainViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignSecureRequestHandler(), new DesignIsAcademiaService() );

            Main.OnNavigatedToAsync();
        }
#endif
    }
}