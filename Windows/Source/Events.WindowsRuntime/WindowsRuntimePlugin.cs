// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Common;
using PocketCampus.Events.Services;
using PocketCampus.Events.ViewModels;
using PocketCampus.Events.Views;
using ThinMvvm;
using ThinMvvm.WindowsRuntime;
using Windows.ApplicationModel.Resources;
using Windows.UI.Xaml;

namespace PocketCampus.Events
{
    public sealed class WindowsRuntimePlugin : Plugin, IWindowsRuntimePlugin
    {
        public string Name
        {
            get { return ResourceLoader.GetForViewIndependentUse( "PocketCampus.Events.WindowsRuntime/Resources" ).GetString( "PluginName" ); }
        }

        public string Icon
        {
            get
            {
                return (string) new ResourceDictionary
                {
                    Source = new Uri( "ms-appx:///PocketCampus.Events.WindowsRuntime/Icons.xaml", UriKind.Absolute )
                }["PluginIcon"];
            }
        }

        public void Initialize( IWindowsRuntimeNavigationService navigationService )
        {
            Container.Bind<ICodeScanner, CodeScanner>();
            Container.Bind<IEmailPrompt, EmailPrompt>();

            navigationService.Bind<CategoryFilterViewModel, CategoryFilterView>();
            navigationService.Bind<EventItemViewModel, EventItemView>();
            navigationService.Bind<EventPoolViewModel, EventPoolView>();
            navigationService.Bind<SettingsViewModel, SettingsView>();
            navigationService.Bind<TagFilterViewModel, TagFilterView>();
        }
    }
}