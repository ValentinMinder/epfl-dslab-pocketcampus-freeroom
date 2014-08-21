// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Data;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Controls;
using PocketCampus.Transport.ViewModels;
using ThinMvvm;

namespace PocketCampus.Transport.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        // HACK: The ListPicker is awful, it can't bind to SelectedItem when the source is null
        private void StationsPicker_Loaded( object sender, RoutedEventArgs e )
        {
            Action bindSelectedItem = () =>
            {
                var binding = new Binding
                {
                    Path = new PropertyPath( "SelectedStation" ),
                    Mode = BindingMode.TwoWay
                };
                StationsPicker.SetBinding( ListPicker.SelectedItemProperty, binding );
            };

            var vm = (MainViewModel) DataContext;
            if ( vm.SelectedStation == null )
            {
                bool ok = false;
                vm.ListenToProperty( x => x.SelectedStation, () =>
                {
                    if ( !ok )
                    {
                        bindSelectedItem();
                        ok = true;
                    }
                } );
            }
            else
            {
                bindSelectedItem();
            }
        }
    }
}