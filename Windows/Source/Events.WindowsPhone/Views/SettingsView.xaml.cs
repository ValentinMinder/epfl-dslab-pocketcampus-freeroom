// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Data;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Controls;

namespace PocketCampus.Events.Views
{
    public partial class SettingsView : BasePage
    {
        public SettingsView()
        {
            InitializeComponent();
        }

        // HACK: The ListPicker has a bug; setting SelectedItem before SelectedIndex is set crashes
        private void PeriodPicker_Loaded( object sender, System.Windows.RoutedEventArgs e )
        {
            var binding = new Binding
            {
                Path = new PropertyPath( "Settings.SearchPeriod" ),
                Mode = BindingMode.TwoWay
            };
            ( (ListPicker) sender ).SetBinding( ListPicker.SelectedItemProperty, binding );
        }
    }
}