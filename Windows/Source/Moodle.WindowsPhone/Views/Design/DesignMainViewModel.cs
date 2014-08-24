// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for MainViewModel

using PocketCampus.Common.Services.Design;
using PocketCampus.Moodle.Services.Design;
using PocketCampus.Moodle.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Moodle.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        private MainViewModel _viewModel;
        protected override MainViewModel ViewModel
        {
            get
            {
                if ( _viewModel == null )
                {
                    _viewModel = new MainViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), new DesignNavigationService(), new DesignMoodleService() );
                    _viewModel.OnNavigatedToAsync().Wait();
                }
                return _viewModel;
            }
        }
#endif
    }
}