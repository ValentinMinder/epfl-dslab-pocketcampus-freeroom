using System;
using PocketCampus.Common;
using ThinMvvm.WindowsRuntime;

namespace PocketCampus.Authentication
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { throw new NotImplementedException(); }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            // TODO
        }
    }
}