// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.CloudPrint.Services;
using PocketCampus.CloudPrint.ViewModels;
using PocketCampus.CloudPrint.Views;
using PocketCampus.Common;
using ThinMvvm;
using ThinMvvm.WindowsRuntime;

namespace PocketCampus.CloudPrint
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
            Container.Bind<IFileLoader, FileLoader>();
            Container.Bind<IFileUploader, FileUploader>();

            navigationService.Bind<MainViewModel, MainView>();
        }
    }
}