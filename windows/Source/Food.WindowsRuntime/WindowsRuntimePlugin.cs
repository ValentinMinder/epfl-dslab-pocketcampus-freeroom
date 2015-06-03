// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Food.ViewModels;
using PocketCampus.Food.Views;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Food
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Food.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon
        {
            get
            {
                return (string) new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Food.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["PluginIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            navigationService.Bind<MainViewModel, MainView>();
            navigationService.Bind<RatingViewModel, RatingView>();
            navigationService.Bind<SettingsViewModel, SettingsView>();
        }
    }
}