// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Windows;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using PocketCampus.Common.Controls;
using PocketCampus.Events.Models;
using PocketCampus.Events.Resources;
using PocketCampus.Events.ViewModels;
using ThinMvvm;

namespace PocketCampus.Events.Views
{
    public partial class EventPoolView : BasePage
    {
        public EventPoolView()
        {
            ApplicationBar = new ApplicationBar();
            ApplicationBar.IsVisible = false;

            Loaded += This_Loaded;
            InitializeComponent();
        }

        private void This_Loaded( object sender, RoutedEventArgs e )
        {
            var vm = (EventPoolViewModel) DataContext;
            vm.ListenToProperty( x => x.Pool, () =>
            {
                var buttons = new List<ApplicationBarIconButton>();
                var items = new List<ApplicationBarMenuItem>();

                // Settings
                if ( vm.Pool.Id == EventPool.RootId )
                {
                    var button = new ApplicationBarIconButton
                    {
                        IconUri = new Uri( "/Assets/Settings.png", UriKind.Relative ),
                        Text = PluginResources.SettingsButton
                    };
                    button.Click += ( _, __ ) => vm.ViewSettingsCommand.Execute();
                    buttons.Add( button );
                }

                // "Right now"
                if ( vm.Pool.Id == EventPool.RootId )
                {
                    var button = new ApplicationBarIconButton
                    {
                        IconUri = new Uri( "/Assets/Now.png", UriKind.Relative ),
                        Text = PluginResources.CurrentEventsButton
                    };
                    button.Click += ( _, __ ) => vm.ShowCurrentEventsCommand.Execute();
                    buttons.Add( button );
                }

                // Filter categories
                if ( vm.FilterByCategoryCommand.CanExecute() )
                {
                    var item = new ApplicationBarMenuItem
                    {
                        Text = PluginResources.FilterCategoriesButton
                    };
                    item.Click += ( _, __ ) => vm.FilterByCategoryCommand.Execute();
                    items.Add( item );
                }

                // Filter tags
                if ( vm.FilterByTagCommand.CanExecute() )
                {
                    var item = new ApplicationBarMenuItem
                    {
                        Text = PluginResources.FilterTagsButton
                    };
                    item.Click += ( _, __ ) => vm.FilterByTagCommand.Execute();
                    items.Add( item );
                }

                // Scan code
                if ( vm.ScanCodeCommand.CanExecute() )
                {
                    var button = new ApplicationBarIconButton
                    {
                        IconUri = new Uri( "/Assets/Camera.png", UriKind.Relative ),
                        Text = PluginResources.ScanCodeButton
                    };
                    button.Click += ( _, __ ) => vm.ScanCodeCommand.Execute();
                    buttons.Add( button );
                }

                // Send favorite email
                if ( vm.RequestFavoriteEmailCommand.CanExecute() )
                {
                    var item = new ApplicationBarMenuItem
                    {
                        Text = PluginResources.SendFavoriteEmailButton
                    };
                    item.Click += ( _, __ ) => vm.RequestFavoriteEmailCommand.ExecuteAsync();
                    items.Add( item );
                }

                ApplicationBar.Buttons.Clear();
                ApplicationBar.MenuItems.Clear();

                if ( buttons.Count + items.Count == 0 )
                {
                    ApplicationBar.IsVisible = false;
                }
                else
                {
                    foreach ( var button in buttons )
                    {
                        ApplicationBar.Buttons.Add( button );
                    }

                    foreach ( var item in items )
                    {
                        ApplicationBar.MenuItems.Add( item );
                    }

                    ApplicationBar.Mode = buttons.Count == 0 ? ApplicationBarMode.Minimized : ApplicationBarMode.Default;
                    ApplicationBar.MatchOverriddenTheme();
                    ApplicationBar.IsVisible = true;
                }
            } );
        }
    }
}