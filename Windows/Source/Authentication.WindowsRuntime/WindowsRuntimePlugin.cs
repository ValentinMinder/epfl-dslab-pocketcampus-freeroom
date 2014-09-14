// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
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
            get { throw new NotSupportedException(); }
        }

        public string Icon
        {
            get { throw new NotSupportedException(); }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}