// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Map.ViewModels;
using PocketCampus.Map.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Map
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Map.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon
        {
            get
            {
                return (string) new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Map.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["PluginIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<SettingsViewModel, SettingsView>();
        }
    }
}