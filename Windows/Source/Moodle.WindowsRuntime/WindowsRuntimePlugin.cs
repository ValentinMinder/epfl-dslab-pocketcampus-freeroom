// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Moodle.Services;
using PocketCampus.Moodle.ViewModels;
using PocketCampus.Moodle.Views;
using ThinMvvm;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Moodle
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Moodle.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon
        {
            get
            {
                return (string) new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Moodle.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["PluginIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            Container.Bind<IFileStorage, FileStorage>();
            Container.Bind<IMoodleDownloader, MoodleDownloader>();

            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<CourseViewModel, CourseView>();
            navigationService.Bind<FileViewModel, FileView>();
        }
    }
}