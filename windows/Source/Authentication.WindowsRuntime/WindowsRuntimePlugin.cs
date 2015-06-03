// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Authentication.ViewModels;
using PocketCampus.Authentication.Views;
using PocketCampus.Common;
using ThinMvvm.WindowsRuntime;

namespace PocketCampus.Authentication
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return null; }
        }

        public string Icon
        {
            get { return null; }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}