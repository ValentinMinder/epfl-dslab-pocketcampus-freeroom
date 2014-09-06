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

        public object Icon
        {
            get { throw new NotSupportedException(); }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}