// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Data;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Controls;

namespace PocketCampus.Food.Views
{
    public partial class SettingsView : BasePage
    {
        public SettingsView()
        {
            InitializeComponent();
        }

        // HACK: The ListPicker has a bug; setting SelectedItem before SelectedIndex is set crashes
        private void ListPicker_Loaded( object sender, RoutedEventArgs e )
        {
            var binding = new Binding
            {
                Path = new PropertyPath( "Settings.PriceTarget" ),
                Mode = BindingMode.TwoWay
            };
            ( (ListPicker) sender ).SetBinding( ListPicker.SelectedItemProperty, binding );
        }
    }
}