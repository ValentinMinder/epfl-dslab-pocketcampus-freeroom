// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for SettingsViewModel

#if DEBUG
using PocketCampus.Events.Services.Design;
#endif
using PocketCampus.Events.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Events.Views.Design
{
    public sealed class DesignSettingsViewModel : DesignViewModel<SettingsViewModel, NoParameter>
    {
#if DEBUG
        protected override SettingsViewModel ViewModel
        {
            get { return new SettingsViewModel( new DesignPluginSettings() ); }
        }
#endif
    }
}