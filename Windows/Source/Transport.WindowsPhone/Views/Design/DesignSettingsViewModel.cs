// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for SettingsViewModel

using PocketCampus.Transport.Services.Design;
using PocketCampus.Transport.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Transport.Views.Design
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